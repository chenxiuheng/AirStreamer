/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Device;

/**
 *
 * @author jasper
 */
public class DeviceRegistry {

    private static final Logger LOGGER = Logger.getLogger(DeviceRegistry.class);
    private Map<String, Device> devices = new HashMap<String, Device>();
    private static DeviceRegistry INSTANCE = null;

    private DeviceRegistry() {
    }

    public static DeviceRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeviceRegistry();
        }
        return INSTANCE;
    }

    public void addDevice(Device device) {
        LOGGER.info("addDevice" + device.getId() + " " + device.getName());
        devices.put(device.getId(), device);
    }

    public void removeDevice(String id) {
        LOGGER.info("removeDevice " + id);
        devices.remove(id);
    }

    public List<Device> getDevices() {
        return new ArrayList<Device>(devices.values());
    }
}
