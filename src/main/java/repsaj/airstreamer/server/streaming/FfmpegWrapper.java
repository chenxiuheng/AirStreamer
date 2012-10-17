/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public class FfmpegWrapper extends StreamConverter implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FfmpegWrapper.class);
    private Process proc = null;
    private final Thread procMonitor = new Thread(this);

    @Override
    protected void doConvert() {
        start(true);
    }

    private void start(boolean async) {

        if (streamInfo != null) {
            LOGGER.info("Starting process... for codec" + streamInfo.getCodec());
        } else {
            LOGGER.info("Starting process... ");
        }


        ProcessBuilder builder = new ProcessBuilder(constructArgs());
        builder.directory(new File(getFilesPath()));
        builder.redirectErrorStream(true);

        try {
            proc = builder.start();

            LOGGER.info("process started");
            procMonitor.start();


            procMonitor.setName("video:" + video.getId() + " codec:" + streamInfo.getCodec());

            for (JobAttachment attachment : jobAttachments) {
                attachment.setMonitorPath(true);
                attachment.start(outputPath, segmentTime);
            }


            if (!async) {
                procMonitor.join();
            }

        } catch (Exception ex) {
            LOGGER.error("error starting process", ex);
        }
    }

    @Override
    public void run() {
        StringBuilder output = new StringBuilder();
        try {
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    int ret = proc.getInputStream().read(buffer);
                    if (ret < 0) {
                        break;
                    } else {
                        String s = new String(buffer);
                        output.append(s);
                        //LOGGER.info("\n" + s);
                    }
                } catch (IOException iex) {
                    LOGGER.error("error reading from process", iex);
                    break;
                }

            }

            int exit = proc.waitFor();
            LOGGER.info("process ended with code " + exit);

            String stringOutput = output.toString();
            LOGGER.info("output:");
            LOGGER.info(stringOutput);

            for (JobAttachment attachment : jobAttachments) {
                attachment.finish();
            }

        } catch (Exception ex) {
            LOGGER.error("error reading from process", ex);
        }
    }

    private List<String> constructArgs() {
        ArrayList<String> args = new ArrayList<String>();

        if (streamInfo != null) {
            switch (streamInfo.getMediaType()) {
                case Audio:
                    args.add("./ffmpeg_a");
                    break;
                case Video:
                    args.add("./ffmpeg_v");
                    break;
                case Subtitle:
                    args.add("./ffmpeg_a");
                    break;
                default:
                    throw new UnsupportedOperationException("Mediatype not supported");
            }
        } else {
            args.add("./ffmpeg_a");
        }

        args.add("-i");
        args.add(video.getPath());



        switch (streamInfo.getMediaType()) {
            case Audio:
                args.add("-c:a");
                break;
            case Video:
                args.add("-c:v");
                break;
            case Subtitle:
                args.add("-c:s");
                break;
            default:
                throw new UnsupportedOperationException("Mediatype not supported");
        }

        if (toCodec == null) {
            args.add("copy");
        } else {

            if (StreamInfo.AAC.equals(toCodec)) {
                args.add("aac");
                args.add("-strict");
                args.add("-2");
                args.add("-b:a");
                args.add("384k");
                args.add("-ac");
                args.add("2");

            } else {
                args.add(toCodec);
            }
        }

        if (streamInfo.getCodec().equalsIgnoreCase("h264")) {
            args.add("-bsf");
            args.add("h264_mp4toannexb");
        }

        args.add("-map");
        args.add("0:" + streamInfo.getIndex());

        if (streamInfo.getMediaType().equals(StreamInfo.MediaType.Subtitle)) {
            args.add(getOutputFile(false, "srt"));
        } else {
            args.add("-f");
            args.add("segment");
            args.add("-segment_time");
            args.add("10");
            args.add(getOutputFile(true, "ts"));
        }

        return args;
    }

    public String getOutputFile(boolean segment, String extension) {

        String tmpPath = outputPath;
        String codec = toCodec != null ? toCodec : streamInfo.getCodec();
        tmpPath += streamInfo.getMediaType() + "_" + codec;
        if (segment) {
            tmpPath += "_%04d";
        }
        tmpPath += "." + extension;
        return tmpPath;
    }
}
