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
public class ServiceWrapper implements Service {

    private List<Service> services = new ArrayList<Service>();

    public void addService(Service service) {
        services.add(service);
    }

    @Override
    public void init() {
        for (Service service : services) {
            service.init();
        }
    }

    @Override
    public void start() {
        for (Service service : services) {
            service.start();
        }
    }

    @Override
    public void stop() {
        for (Service service : services) {
            service.stop();
        }
    }
}
