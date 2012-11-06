/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.ApplicationSettings;
import repsaj.airstreamer.server.airplay.*;
import repsaj.airstreamer.server.model.Device;

/**
 *
 * @author jasper
 */
public class AirPlayPlayer extends HLSStreamConverter {

    private static final Logger LOGGER = Logger.getLogger(AirPlayPlayer.class);
    private static final String PLAYLIST = "airplayer.m3u8";
    private ApplicationSettings applicationSettings;
    private AirPlayDeviceConnection connection;
    private Timer playBackMonitorTimer;
    private List<String> videoCodecs = new ArrayList<String>();
    private List<String> audioCodecs = new ArrayList<String>();
    private List<String> subtitleCodecs = new ArrayList<String>();
    private TimerTask playbackMonitor = new TimerTask() {
        @Override
        public void run() {
            PlayBackInfoCommand playback = new PlayBackInfoCommand();
            DeviceResponse tvresponse = connection.sendCommand(playback);
            LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getContent());
            if (tvresponse.getContent().indexOf("position") == -1) {
                LOGGER.info("CANCEL");
                //The device stopped playing the video, so stop this player
                stop();
            }
        }
    };

    public AirPlayPlayer(ApplicationSettings applicationSettings) {
        super(applicationSettings.getTmpPath());
        this.applicationSettings = applicationSettings;
        videoCodecs.add(StreamInfo.H264);
        audioCodecs.add(StreamInfo.AAC);
        audioCodecs.add(StreamInfo.AC3);
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

        Device device = session.getExternalDevice();
        PlayCommand cmd = new PlayCommand("http://" + applicationSettings.getIp() + ":" + applicationSettings.getPort() + "/files/video/" + video.getId() + "/" + PLAYLIST, 0);

        connection = new AirPlayDeviceConnection(device);
        DeviceResponse tvresponse = connection.sendCommand(cmd);
        LOGGER.info("play response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());

        if (playBackMonitorTimer != null) {
            playBackMonitorTimer.cancel();
        }
        playBackMonitorTimer = new Timer();
        playBackMonitorTimer.schedule(playbackMonitor, 10000, 5000);

    }

    @Override
    protected void doStop() {
        if (playBackMonitorTimer != null) {
            playBackMonitorTimer.cancel();
        }

        StopCommand cmd = new StopCommand();
        DeviceResponse tvresponse = connection.sendCommand(cmd);
        LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());
        connection.close();

    }

    @Override
    public List<String> getVideoCodecs() {
        return videoCodecs;
    }

    @Override
    public List<String> getAudioCodecs() {
        //TODO check for ac3 capable receiver
        return audioCodecs;
    }

    @Override
    public List<String> getSubtitleCodecs() {
        return subtitleCodecs;
    }
}
