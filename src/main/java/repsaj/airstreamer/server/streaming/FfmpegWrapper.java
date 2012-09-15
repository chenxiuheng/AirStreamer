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
public class FfmpegWrapper extends StreamTranscoder implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FfmpegWrapper.class);
    private Process proc = null;
    private final Thread procMonitor = new Thread(this);
    private final Video video;
    private String filesPath;
    private StreamInfo streamInfo;
    private MediaInfo mediaInfo;
    private String toCodec;
    private boolean infoOnly = false;
    private HttpLiveStreamingPlaylistGenerator httpLiveStreamingPlaylistGenerator;

    public FfmpegWrapper(String filesPath, Video video, StreamInfo streamInfo) {
        this.filesPath = filesPath;
        this.video = video;
        this.streamInfo = streamInfo;

    }

    public FfmpegWrapper(Video video) {
        this.video = video;
        infoOnly = true;
    }

    public void setToCodec(String toCodec) {
        this.toCodec = toCodec;
    }

    public void start() {
        start(true);
    }

    public void start(boolean async) {

        if (streamInfo != null) {
            LOGGER.info("Starting process... for codec" + streamInfo.getCodec());
        } else {
            LOGGER.info("Starting process... ");
        }

        //ensure output dir exists
        if (!infoOnly) {
            String tmpOutputPath = getOutputPath();
            File dir = new File(tmpOutputPath);
            LOGGER.info("creating directory " + dir.getPath());
            dir.mkdirs();
        }

        ProcessBuilder builder = new ProcessBuilder(constructArgs());
        builder.directory(new File("/Users/jasper/Documents/movie_tmp/"));
        builder.redirectErrorStream(true);

        try {
            proc = builder.start();

            LOGGER.info("process started");
            procMonitor.start();

            if (!infoOnly) {
                procMonitor.setName("video:" + video.getId() + " codec:" + streamInfo.getCodec());
                httpLiveStreamingPlaylistGenerator = new HttpLiveStreamingPlaylistGenerator(getOutputPath(), 10);
                httpLiveStreamingPlaylistGenerator.start();
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

            if (httpLiveStreamingPlaylistGenerator != null) {
                httpLiveStreamingPlaylistGenerator.finish();
            }

            if (infoOnly) {
                //parse output

                MediaInfo info = new MediaInfo();
                int counter = 0;
                int index = 0;

                while (index != -1) {
                    index = stringOutput.indexOf("Stream", counter);
                    if (index > 0) {
                        counter = stringOutput.indexOf("\n", index);
                        String streamString = stringOutput.substring(index, counter);
                        //Stream #0:0(eng): Video: h264 (High), yuv420p, 1280x720 [SAR 1:1 DAR 16:9], 23.98 fps, 23.98 tbr, 1k tbn, 47.95 tbc (default)
                        //Stream #0:1(eng): Audio: ac3, 48000 Hz, 5.1(side), s16, 640 kb/s (default)
                        //Stream #0:2(eng): Subtitle: subrip (default)
                        String[] streamItems = streamString.split(":");

                        StreamInfo sInfo = new StreamInfo();

                        String[] indexLanguages = streamItems[1].split("\\(");
                        sInfo.setIndex(Integer.valueOf(indexLanguages[0]));
                        if (indexLanguages.length > 1) {
                            sInfo.setLanguage(indexLanguages[1].substring(0, indexLanguages[1].length() - 1));
                        }

                        sInfo.setMediaType(StreamInfo.MediaType.valueOf(streamItems[2].trim()));
                        String[] codecItems = streamItems[3].split(",");
                        String[] codecStringItems = codecItems[0].trim().split(" ");
                        sInfo.setCodec(codecStringItems[0]);
                        info.getStreams().add(sInfo);


                    }
                }
                this.mediaInfo = info;

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

        if (infoOnly) {
            return args;
        }

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

    /**
     * @return the mediaInfo
     */
    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }
}
