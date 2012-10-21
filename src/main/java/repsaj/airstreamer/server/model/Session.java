/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

import java.util.UUID;
import repsaj.airstreamer.server.streaming.StreamPlayer;

/**
 *
 * @author jasper
 */
public class Session {

    private final String sessionId;
    private Device externalDevice;
    private StreamPlayer player;

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
    }

    public String getSessionId() {
        return sessionId;
    }

    public Device getExternalDevice() {
        return externalDevice;
    }

    public void setExternalDevice(Device externalDevice) {
        this.externalDevice = externalDevice;
    }

    public StreamPlayer getPlayer() {
        return player;
    }

    public void setPlayer(StreamPlayer player) {
        this.player = player;
    }
}
