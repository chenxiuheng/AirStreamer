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
public class StreamAnalyzer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(StreamAnalyzer.class);
    private final Thread procMonitor = new Thread(this);
    private Process proc = null;
    private MediaInfo mediaInfo;
    private Video video;
    private String path;

    public StreamAnalyzer(String path) {
        this.path = path;
    }

    public MediaInfo analyze(Video video) {
        this.mediaInfo = new MediaInfo();
        this.video = video;

        ProcessBuilder builder = new ProcessBuilder(constructArgs());
        builder.directory(new File(path));
        builder.redirectErrorStream(true);

        try {
            proc = builder.start();

            LOGGER.info("process started");
            procMonitor.start();
            procMonitor.join();


        } catch (Exception ex) {
            LOGGER.error("error starting process", ex);
        }

        return mediaInfo;
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


            //parse output

            int counter = 0;
            int index = 0;

            while (index != -1) {
                index = stringOutput.indexOf("Stream", counter);
                if (index > 0) {
                    counter = stringOutput.indexOf("\n", index);
                    String streamString = stringOutput.substring(index, counter);
                    LOGGER.info(streamString);
                    //Stream #0:0(eng): Video: h264 (High), yuv420p, 1280x720 [SAR 1:1 DAR 16:9], 23.98 fps, 23.98 tbr, 1k tbn, 47.95 tbc (default)
                    //Stream #0:1(eng): Audio: ac3, 48000 Hz, 5.1(side), s16, 640 kb/s (default)
                    //Stream #0:2(eng): Subtitle: subrip (default)
                    try {
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
                        mediaInfo.getStreams().add(sInfo);
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error("error reading from process", ex);
        }
    }

    private List<String> constructArgs() {
        ArrayList<String> args = new ArrayList<String>();

        args.add("./ffmpeg_v");
        args.add("-i");
        args.add(video.getPath());

        return args;
    }
}
