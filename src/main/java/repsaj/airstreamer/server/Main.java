/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.airplay.AirPlayJmDNSService;
import repsaj.airstreamer.server.db.MongoDatabase;
import repsaj.airstreamer.server.metadata.MetaDataUpdater;
import repsaj.airstreamer.server.metadata.ResourceManager;
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
        settings.load();

        LOGGER.info("Starting database...");
        final MongoDatabase db = new MongoDatabase();
        db.init();
        db.start();

        LOGGER.info("Starting services...");
        serviceWrapper = new ServiceWrapper(settings, db);

        serviceWrapper.addService(new WebService());
        serviceWrapper.addService(new AirPlayJmDNSService());
        serviceWrapper.addService(MetaDataUpdater.getInstance());
        serviceWrapper.addService(ResourceManager.getInstance());

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
