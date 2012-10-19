/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import java.util.List;
import repsaj.airstreamer.server.DeviceRegistry;
import repsaj.airstreamer.server.model.Device;

/**
 *
 * @author jasper
 */
public class AppHandler {

    public List<Device> listDevices() {
        return DeviceRegistry.getInstance().getDevices();
    }
}
