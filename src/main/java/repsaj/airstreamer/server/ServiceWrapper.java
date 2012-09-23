/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import java.util.ArrayList;
import java.util.List;
import repsaj.airstreamer.server.db.Database;

/**
 *
 * @author jasper
 */
public class ServiceWrapper {

    private List<Service> services = new ArrayList<Service>();
    private ApplicationSettings applicationSettings;
    private Database db;

    public ServiceWrapper(ApplicationSettings applicationSettings, Database db) {
        this.applicationSettings = applicationSettings;
        this.db = db;
    }

    public void addService(Service service) {
        service.setApplicationSettings(applicationSettings);
        service.setDatabase(db);
        services.add(service);
        
    }

    public void init() {
        for (Service service : services) {
            service.init();
        }
    }

    public void start() {
        for (Service service : services) {
            service.start();
        }
    }

    public void stop() {
        for (Service service : services) {
            service.stop();
        }
    }
}
