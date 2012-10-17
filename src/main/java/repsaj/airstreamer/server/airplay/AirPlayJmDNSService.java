/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.airplay;

import java.io.IOException;
import java.net.InetAddress;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.DeviceRegistry;
import repsaj.airstreamer.server.Service;

/**
 *
 * @author jasper
 */
public class AirPlayJmDNSService extends Service implements ServiceListener {

    private static final Logger LOGGER = Logger.getLogger(AirPlayJmDNSService.class);
    private JmDNS jmDNS = null;

    @Override
    public void init() {
        try {
            InetAddress addr = InetAddress.getByName(getApplicationSettings().getIp());
            jmDNS = JmDNS.create(addr);
        } catch (IOException ex) {
            LOGGER.error("unable to init JmDNS", ex);
        }
    }

    @Override
    public void start() {
        jmDNS.addServiceListener("_airplay._tcp.local.", this);
    }

    @Override
    public void stop() {

        if (jmDNS != null) {
            try {
                jmDNS.close();
            } catch (IOException ex) {
                LOGGER.error("error while stopping JmDNS", ex);
            }
        }
    }

    @Override
    public void serviceAdded(ServiceEvent serviceEvent) {
        LOGGER.info("serviceAdded: " + serviceEvent);

        ServiceInfo serviceInfo = serviceEvent.getInfo();
        if (serviceInfo == null || serviceInfo.getInetAddresses().length == 0) {
            serviceInfo = serviceEvent.getDNS().getServiceInfo(serviceEvent.getType(), serviceEvent.getName(), 2000);
        }

        Device device = new Device(serviceInfo.getName(), serviceInfo.getAddress(), serviceInfo.getPort());
        DeviceRegistry.getInstance().addDevice(device);
    }

    @Override
    public void serviceRemoved(ServiceEvent serviceEvent) {
        LOGGER.info("serviceRemoved: " + serviceEvent);

        ServiceInfo serviceInfo = serviceEvent.getInfo();
        if (serviceInfo == null || serviceInfo.getInetAddresses().length == 0) {
            serviceInfo = serviceEvent.getDNS().getServiceInfo(serviceEvent.getType(), serviceEvent.getName(), 2000);
        }

        Device device = new Device(serviceInfo.getName(), serviceInfo.getAddress(), serviceInfo.getPort());
        DeviceRegistry.getInstance().removeDevice(device.getId());
    }

    @Override
    public void serviceResolved(ServiceEvent serviceEvent) {
        LOGGER.info("serviceResolved: " + serviceEvent);
    }
}
