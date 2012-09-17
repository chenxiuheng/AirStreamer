/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.airplay.*;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.model.Subtitle;

/**
 *
 * @author jasper
 */
public class AirPlayPlayer extends StreamPlayer {

    private static final Logger LOGGER = Logger.getLogger(AirPlayPlayer.class);
    private AirPlayDeviceConnection connection;
    private Timer playBackMonitorTimer;
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

    @Override
    protected void doPrepare() {
        boolean subtitleMatch = false;
        //TODO replace with setting
        String subtitleLanguage = "en";

        //First check for external subtitles
        for (Subtitle sub : video.getSubtitles()) {
            if (sub.getLanguage().equals(subtitleLanguage)) {
                subtitleMatch = true;

                StreamInfo streamInfo = new StreamInfo();
                streamInfo.setCodec(StreamInfo.SUBRIP);
                streamInfo.setIndex(0);
                streamInfo.setLanguage(sub.getLanguage());
                streamInfo.setMediaType(StreamInfo.MediaType.Subtitle);

                SrtToWebvvt srtToWebvvt = new SrtToWebvvt(new File(sub.getPath()));
                srtToWebvvt.addAttachment(new HLSPlaylistGenerator());
                srtToWebvvt.setFilesPath(path);
                srtToWebvvt.convertStream(video, streamInfo, StreamInfo.WEBVVT);
                break;
            }
        }


        for (StreamInfo stream : mediaInfo.getStreams()) {
            switch (stream.getMediaType()) {
                case Audio:
                    if (stream.getCodec().equals(StreamInfo.AAC)) {
                        FfmpegWrapper ffmpegWrapper = new FfmpegWrapper();
                        ffmpegWrapper.setFilesPath(path);
                        ffmpegWrapper.addAttachment(new HLSPlaylistGenerator());
                        ffmpegWrapper.convertStream(video, stream, null);

                    } else if (stream.getCodec().equals(StreamInfo.AC3)) {

                        //Extract ac3 stream:
                        FfmpegWrapper ffmpegWrapper1 = new FfmpegWrapper();
                        ffmpegWrapper1.setFilesPath(path);
                        ffmpegWrapper1.addAttachment(new HLSPlaylistGenerator());
                        ffmpegWrapper1.convertStream(video, stream, null);

                        //Convert ac3 to aac stream:
                        FfmpegWrapper ffmpegWrapper2 = new FfmpegWrapper();
                        ffmpegWrapper2.setFilesPath(path);
                        ffmpegWrapper2.addAttachment(new HLSPlaylistGenerator());
                        ffmpegWrapper2.convertStream(video, stream, "libfaac");


                    } else {
                        throw new UnsupportedOperationException("Codec not supported:" + stream.getCodec());
                    }
                    break;

                case Video:
                    if (!stream.getCodec().equals(StreamInfo.H264)) {
                        throw new UnsupportedOperationException("Codec not supported:" + stream.getCodec());
                    }

                    FfmpegWrapper ffmpegWrapper = new FfmpegWrapper();
                    ffmpegWrapper.setFilesPath(path);
                    ffmpegWrapper.addAttachment(new HLSPlaylistGenerator());
                    ffmpegWrapper.convertStream(video, stream, null);

                    break;

                case Subtitle:
                    if (!stream.getCodec().equals(StreamInfo.SUBRIP)) {
                        throw new UnsupportedOperationException("Codec not supported:" + stream.getCodec());
                    }

                    if (!subtitleMatch && stream.getLanguage().equals("eng")) {
                        FfmpegWrapper ffmpegWrappersub = new FfmpegWrapper();
                        ffmpegWrappersub.setFilesPath(path);

                        File output = new File(ffmpegWrappersub.getOutputFile(false, "srt"));
                        SrtToWebvvt srtToWebvvt = new SrtToWebvvt(output);
                        srtToWebvvt.setUpAsAttachment(video, stream, StreamInfo.WEBVVT);

                        ffmpegWrappersub.addAttachment(srtToWebvvt);
                        ffmpegWrappersub.convertStream(video, stream, null);
                        subtitleMatch = true;
                    }

                    break;
            }
        }

        //TODO Generate master playlist
    }

    @Override
    protected void doPlay() {

        Device device = session.getExternalDevice();

        PlayCommand cmd = new PlayCommand("http://192.168.1.13:8085/files/video/" + video.getId() + "/index.m3u8", 0);

        connection = new AirPlayDeviceConnection(device);
        DeviceResponse tvresponse = connection.sendCommand(cmd);
        LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());

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
}
