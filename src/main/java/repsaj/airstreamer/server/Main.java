/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.airplay.AirPlayJmDNSService;
import repsaj.airstreamer.server.streaming.MediaInfo;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.streaming.FfmpegWrapper;
import repsaj.airstreamer.server.webserver.WebService;

/**
 *
 * @author jwesselink
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private static ServiceWrapper serviceWrapper;

    public static void main(String[] args) {

        BasicConfigurator.configure();

        LOGGER.info("Starting...");

        ApplicationSettings settings = new ApplicationSettings();
        settings.setPath("/Users/jasper/Documents/movie_tmp/");

        serviceWrapper = new ServiceWrapper(settings);

        Video video = new Video();
        video.setId("1");
        video.setName("Californication");
        video.setPath("/Users/jasper/Documents/movie_tmp/cali.mkv");
        VideoRegistry.getInstance().addVideo(video);

        video = new Video();
        video.setId("2");
        video.setName("Rob Dyrdek");
        video.setPath("/Users/jasper/Documents/movie_tmp/rob.mkv");
        VideoRegistry.getInstance().addVideo(video);

        video = new Video();
        video.setId("3");
        video.setName("Foo fighters");
        video.setPath("/Users/jasper/Documents/movie_tmp/foo.mkv");
        VideoRegistry.getInstance().addVideo(video);

        serviceWrapper.addService(new WebService());
        serviceWrapper.addService(new AirPlayJmDNSService());

        serviceWrapper.init();
        serviceWrapper.start();

        
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    serviceWrapper.stop();
                } catch (Exception e) {
                }
            }
        });

    }
}
