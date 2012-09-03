/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.airplay.AirPlayJmDNSService;
import repsaj.airstreamer.server.airplay.DeviceConnection;
import repsaj.airstreamer.server.airplay.DeviceResponse;
import repsaj.airstreamer.server.airplay.PlayCommand;
import repsaj.airstreamer.server.streaming.FfmpegWrapper;
import repsaj.airstreamer.server.streaming.HttpLiveStreamingPlaylistGenerator;
import repsaj.airstreamer.server.webserver.WebService;

/**
 *
 * @author jwesselink
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private static ServiceWrapper serviceWrapper = new ServiceWrapper();

    public static void main(String[] args) {

        BasicConfigurator.configure();

        LOGGER.info("Starting...");

        serviceWrapper.addService(new WebService());
        serviceWrapper.addService(new AirPlayJmDNSService());

        serviceWrapper.init();
        serviceWrapper.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }

//        if (!DeviceRegistry.getInstance().getDevices().isEmpty()) {
//
//            //PlayCommand cmd = new PlayCommand("http://trailers.apple.com/movies/independent/stolen/stolen-tlr2_h720p.mov", 0);
//            PlayCommand cmd = new PlayCommand("http://192.168.1.13:8085/files/index.m3u8", 0);
//            DeviceConnection conn = new DeviceConnection((Device) DeviceRegistry.getInstance().getDevices().toArray()[0]);
//            DeviceResponse response = conn.sendCommand(cmd);
//
//            LOGGER.info("response: " + response.getResponseCode() + " " + response.getResponseMessage());
//        }


//        FfmpegWrapper ffmpegWrapper = new FfmpegWrapper();
//        ffmpegWrapper.start();


//        PlayListGenerator generator = new PlayListGenerator("/Users/jasper/Documents/movie_tmp/");
//        generator.start();
//         try {
//            Thread.sleep(1500);
//        } catch (InterruptedException ex) {
//        }
//        generator.finish();



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
