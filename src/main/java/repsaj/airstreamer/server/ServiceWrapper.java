/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jasper
 */
public class ServiceWrapper {

    private List<Service> services = new ArrayList<Service>();
    private ApplicationSettings applicationSettings;

    public ServiceWrapper(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

    public void addService(Service service) {
        service.setApplicationSettings(applicationSettings);
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
