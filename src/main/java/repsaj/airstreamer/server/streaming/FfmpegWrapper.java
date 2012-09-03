/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public class FfmpegWrapper implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FfmpegWrapper.class);
    private Process proc = null;
    private Thread procMonitor = new Thread(this);
    private HttpLiveStreamingPlaylistGenerator generator = new HttpLiveStreamingPlaylistGenerator("/Users/jasper/Documents/movie_tmp");

    public FfmpegWrapper() {
    }

    public void start() {
        LOGGER.info("Starting process...");
        ProcessBuilder builder = new ProcessBuilder("./segment.sh");
        builder.directory(new File("/Users/jasper/Documents/movie_tmp"));

        try {
            proc = builder.start();
            LOGGER.info("process started");
            generator.start();
            procMonitor.start();
        } catch (Exception ex) {
            LOGGER.error("error starting process", ex);
        }
    }

    @Override
    public void run() {
        try {
            int ret = proc.waitFor();
            LOGGER.info("process ended with code " + ret);
            generator.finish();
        } catch (InterruptedException e) {
        }
    }
}
