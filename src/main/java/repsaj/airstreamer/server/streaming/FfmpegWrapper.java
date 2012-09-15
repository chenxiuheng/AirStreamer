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
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public class FfmpegWrapper extends StreamMuxer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FfmpegWrapper.class);
    private Process proc = null;
    private final Thread procMonitor = new Thread(this);
    private HttpLiveStreamingPlaylistGenerator httpLiveStreamingPlaylistGenerator;

    @Override
    protected void doMux() {
        start(true);
    }

    public void start(boolean async) {

        if (streamInfo != null) {
            LOGGER.info("Starting process... for codec" + streamInfo.getCodec());
        } else {
            LOGGER.info("Starting process... ");
        }

        //ensure output dir exists

        String tmpOutputPath = getOutputPath();
        File dir = new File(tmpOutputPath);
        LOGGER.info("creating directory " + dir.getPath());
        dir.mkdirs();


        ProcessBuilder builder = new ProcessBuilder(constructArgs());
        builder.directory(new File("/Users/jasper/Documents/movie_tmp/"));
        builder.redirectErrorStream(true);

        try {
            proc = builder.start();

            LOGGER.info("process started");
            procMonitor.start();


            procMonitor.setName("video:" + video.getId() + " codec:" + streamInfo.getCodec());
            httpLiveStreamingPlaylistGenerator = new HttpLiveStreamingPlaylistGenerator(getOutputPath(), 10);
            httpLiveStreamingPlaylistGenerator.start();


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

            if (httpLiveStreamingPlaylistGenerator != null) {
                httpLiveStreamingPlaylistGenerator.finish();
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
            args.add(toCodec);
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

    private String getOutputFile(boolean segment, String extension) {

        String outputPath = getOutputPath();
        String codec = toCodec != null ? toCodec : streamInfo.getCodec();
        outputPath += streamInfo.getMediaType() + "_" + codec;
        if (segment) {
            outputPath += "_%04d";
        }
        outputPath += "." + extension;
        return outputPath;
    }

    private String getOutputPath() {

        String outputPath = filesPath + "video/";
        outputPath += video.getId() + "/";

        String codec = toCodec != null ? toCodec : streamInfo.getCodec();
        outputPath += streamInfo.getMediaType() + "_" + codec + "/";


        return outputPath;
    }
}
