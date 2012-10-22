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
public class WebPlayer extends HLSStreamConverter {

    private List<String> videoCodecs = new ArrayList<String>();
    private List<String> audioCodecs = new ArrayList<String>();
    private List<String> subtitleCodecs = new ArrayList<String>();

    public WebPlayer(String tmpPath) {
        super(tmpPath);
        videoCodecs.add(StreamInfo.H264);
        audioCodecs.add(StreamInfo.AAC);
        subtitleCodecs.add(StreamInfo.WEBVVT);
    }

    @Override
    protected void doPlay() {
        //Play is handled by client
    }

    @Override
    protected void doStop() {
        //Stop is handled by client
    }

    @Override
    public List<String> getVideoCodecs() {
        return videoCodecs;
    }

    @Override
    public List<String> getAudioCodecs() {
        return audioCodecs;
    }

    @Override
    public List<String> getSubtitleCodecs() {
        return subtitleCodecs;
    }
}
