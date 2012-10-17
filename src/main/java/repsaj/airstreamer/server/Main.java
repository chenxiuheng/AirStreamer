/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.airplay.AirPlayJmDNSService;
import repsaj.airstreamer.server.db.MongoDatabase;
import repsaj.airstreamer.server.metadata.MetaDataUpdater;
import repsaj.airstreamer.server.webserver.WebService;

/**
 *
 * @author jwesselink
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    public static ServiceWrapper serviceWrapper;

    public static void main(String[] args) {

        BasicConfigurator.configure();

        LOGGER.info("Starting...");

        ApplicationSettings settings = new ApplicationSettings();
        settings.load();


        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            LOGGER.info("ip:" + ip);
        } catch (UnknownHostException ex) {
            LOGGER.error("Error getting ip of machine", ex);
        }

        LOGGER.info("Starting database...");
        final MongoDatabase db = new MongoDatabase();
        db.init();
        db.start();

        LOGGER.info("Starting services...");
        serviceWrapper = new ServiceWrapper(settings, db);

        serviceWrapper.addService(new WebService());
        serviceWrapper.addService(new AirPlayJmDNSService());
        serviceWrapper.addService(new MetaDataUpdater());

        serviceWrapper.init();
        serviceWrapper.start();


        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    serviceWrapper.stop();
                    db.stop();
                } catch (Exception e) {
                }
            }
        });

    }
}
