/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.ApplicationSettings;
import repsaj.airstreamer.server.airplay.*;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.model.Subtitle;

/**
 *
 * @author jasper
 */
public class AirPlayPlayer extends StreamPlayer {

    private static final Logger LOGGER = Logger.getLogger(AirPlayPlayer.class);
    private ApplicationSettings applicationSettings;
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

    public AirPlayPlayer(ApplicationSettings applicationSettings) {
        super(applicationSettings.getTmpPath());
        this.applicationSettings = applicationSettings;
    }

    @Override
    protected void doPrepare() {
        try {
            boolean subtitleMatch = false;
            //TODO replace with setting

            ArrayList<String> subtitleLanguages = new ArrayList<String>();
            subtitleLanguages.add("def");
            subtitleLanguages.add("nl");
            subtitleLanguages.add("en");
            subtitleLanguages.add("eng");

            ArrayList<StreamInfo> outputStreams = new ArrayList<StreamInfo>();


            //First check for external subtitles

            for (String language : subtitleLanguages) {
                for (Subtitle sub : video.getSubtitles()) {
                    if (sub.getLanguage().equals(language)) {
                        subtitleMatch = true;

                        StreamInfo streamInfo = new StreamInfo();
                        streamInfo.setCodec(StreamInfo.SUBRIP);
                        streamInfo.setIndex(0);
                        streamInfo.setLanguage(sub.getLanguage());
                        streamInfo.setMediaType(StreamInfo.MediaType.Subtitle);

                        SrtToWebvvt srtToWebvvt = new SrtToWebvvt(new File(sub.getPath()));
                        srtToWebvvt.addAttachment(new HLSPlaylistGenerator());
                        srtToWebvvt.setFilesPath(tmpPath);
                        srtToWebvvt.convertStream(video, streamInfo, StreamInfo.WEBVVT);

                        StreamInfo outputStream = (StreamInfo) streamInfo.clone();
                        outputStream.setCodec(StreamInfo.WEBVVT);
                        outputStreams.add(outputStream);
                        break;
                    }
                }
                if (subtitleMatch) {
                    break;
                }
            }


            for (StreamInfo stream : mediaInfo.getStreams()) {
                switch (stream.getMediaType()) {
                    case Audio:
                        if (stream.getCodec().equals(StreamInfo.AAC)) {
                            FfmpegWrapper ffmpegWrapper = new FfmpegWrapper();
                            ffmpegWrapper.setFilesPath(tmpPath);
                            ffmpegWrapper.addAttachment(new HLSPlaylistGenerator());
                            ffmpegWrapper.convertStream(video, stream, null);

                            StreamInfo outputStream = (StreamInfo) stream.clone();
                            outputStreams.add(outputStream);

                        } else if (stream.getCodec().equals(StreamInfo.AC3)) {

                            //Extract ac3 stream:
                            FfmpegWrapper ffmpegWrapper1 = new FfmpegWrapper();
                            ffmpegWrapper1.setFilesPath(tmpPath);
                            ffmpegWrapper1.addAttachment(new HLSPlaylistGenerator());
                            ffmpegWrapper1.convertStream(video, stream, null);

                            StreamInfo outputStream1 = (StreamInfo) stream.clone();
                            outputStreams.add(outputStream1);


                            //Convert ac3 to aac stream:
//                            FfmpegWrapper ffmpegWrapper2 = new FfmpegWrapper();
//                            ffmpegWrapper2.setFilesPath(tmpPath);
//                            ffmpegWrapper2.addAttachment(new HLSPlaylistGenerator());
//                            ffmpegWrapper2.convertStream(video, stream, StreamInfo.AAC);
//
//                            StreamInfo outputStream2 = (StreamInfo) stream.clone();
//                            outputStream2.setCodec(StreamInfo.AAC);
//                            outputStreams.add(outputStream2);

                        }
                        break;

                    case Video:
                        if (!stream.getCodec().equals(StreamInfo.H264)) {
                            throw new UnsupportedOperationException("Codec not supported:" + stream.getCodec());
                        }

                        FfmpegWrapper ffmpegWrapper = new FfmpegWrapper();
                        ffmpegWrapper.setFilesPath(tmpPath);
                        ffmpegWrapper.addAttachment(new HLSPlaylistGenerator());
                        ffmpegWrapper.convertStream(video, stream, null);

                        StreamInfo outputStream = (StreamInfo) stream.clone();
                        outputStreams.add(outputStream);

                        break;

                    case Subtitle:

                        if (!subtitleMatch && stream.getCodec().equals(StreamInfo.SUBRIP) && subtitleLanguages.contains(stream.getLanguage())) {
                            FfmpegWrapper ffmpegWrappersub = new FfmpegWrapper();
                            ffmpegWrappersub.setFilesPath(tmpPath);

                            File output = new File(ffmpegWrappersub.getOutputFile(false, "srt"));
                            SrtToWebvvt srtToWebvvt = new SrtToWebvvt(output);
                            srtToWebvvt.setUpAsAttachment(video, stream, StreamInfo.WEBVVT);

                            ffmpegWrappersub.addAttachment(srtToWebvvt);
                            ffmpegWrappersub.convertStream(video, stream, null);
                            subtitleMatch = true;

                            StreamInfo outputStreamSub = (StreamInfo) stream.clone();
                            outputStreamSub.setCodec(StreamInfo.WEBVVT);
                            outputStreams.add(outputStreamSub);
                        }

                        break;
                }
            }

            HLSMasterPlaylistGenerator masterPlaylistGenerator = new HLSMasterPlaylistGenerator();
            masterPlaylistGenerator.start(outputStreams, tmpPath + "video/" + video.getId() + "/");
            //Wait for the playlists to be generated.
            Thread.sleep(2000);

        } catch (Exception ex) {
            LOGGER.error("error in prepare player", ex);
        }
    }

    @Override
    protected void doPlay() {

        Device device = session.getExternalDevice();
        PlayCommand cmd = new PlayCommand("http://" + applicationSettings.getIp() + ":8085/files/video/" + video.getId() + "/index.m3u8", 0);

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
}
