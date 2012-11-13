/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jasper
 */
public class MediaInfo {
    private List<StreamInfo> streams = new ArrayList<StreamInfo>();

    /**
     * @return the streams
     */
    public List<StreamInfo> getStreams() {
        return streams;
    }

    /**
     * @param streams the streams to set
     */
    public void setStreams(List<StreamInfo> streams) {
        this.streams = streams;
    }
}
