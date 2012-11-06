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

    private static final String PLAYLIST = "index.m3u8";
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
    protected List<StreamInfo> doPrepare() {
        List<StreamInfo> outputStreams = super.doPrepare();

        HLSMasterPlaylistGenerator masterPlaylistGenerator = new HLSMasterPlaylistGenerator();
        masterPlaylistGenerator.start(outputStreams, tmpPath + "video/" + video.getId() + "/", PLAYLIST);
        //Wait for the playlists to be generated.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
        }

        return outputStreams;
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
