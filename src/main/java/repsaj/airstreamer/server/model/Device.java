/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public class Device {
    private static final Logger LOGGER = Logger.getLogger(Device.class);

    private String id;
    private String name;
    private InetAddress inetAddress;
    private int port;

    public Device(String name, InetAddress inetAddress, int port) {
        this.name = name;
        this.inetAddress = inetAddress;
        this.port = port;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(inetAddress.getAddress());
            messageDigest.update(String.valueOf(port).getBytes("UTF-8"));
            id = String.format("%032X", new BigInteger(1, messageDigest.digest()));
        } catch (Exception e) {
            LOGGER.error("error generating id", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the inetAddress
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * @param inetAddress the inetAddress to set
     */
    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
}
