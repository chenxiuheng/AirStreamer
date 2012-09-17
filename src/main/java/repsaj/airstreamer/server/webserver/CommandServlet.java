/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.DeviceRegistry;
import repsaj.airstreamer.server.VideoRegistry;
import repsaj.airstreamer.server.airplay.DeviceConnection;
import repsaj.airstreamer.server.airplay.DeviceResponse;
import repsaj.airstreamer.server.airplay.ErrorLogCommand;
import repsaj.airstreamer.server.airplay.PlayBackInfoCommand;
import repsaj.airstreamer.server.airplay.PlayCommand;
import repsaj.airstreamer.server.airplay.ScrubCommand;
import repsaj.airstreamer.server.airplay.StopCommand;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.model.Subtitle;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.streaming.FfmpegWrapper;
import repsaj.airstreamer.server.streaming.HLSPlaylistGenerator;
import repsaj.airstreamer.server.streaming.MediaInfo;
import repsaj.airstreamer.server.streaming.SrtToWebvvt;
import repsaj.airstreamer.server.streaming.StreamAnalyzer;
import repsaj.airstreamer.server.streaming.StreamInfo;

/**
 *
 * @author jasper
 */
public class CommandServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CommandServlet.class);
    private String path;
    private Timer timer;

    public CommandServlet(String path) {
        this.path = path;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String command = request.getParameter("command");

        if ("test".equals(command)) {
            String videoId = request.getParameter("id");

            if (!DeviceRegistry.getInstance().getDevices().isEmpty()) {

                PlayCommand cmd = new PlayCommand("http://192.168.1.13:8085/files/video/" + videoId + "/index.m3u8", 0);

                Device device = (Device) DeviceRegistry.getInstance().getDevices().toArray()[0];

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }

                final DeviceConnection conn = new DeviceConnection(device);
                DeviceResponse tvresponse = conn.sendCommand(cmd);

                LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());

                //TODO Move this code and introduce a session manager
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        PlayBackInfoCommand playback = new PlayBackInfoCommand();
                        DeviceResponse tvresponse = conn.sendCommand(playback);
                        LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getContent());
                        if (tvresponse.getContent().indexOf("position") == -1) {
                            LOGGER.info("CANCEL");
                            conn.close();
                            timer.cancel();
                        }

                    }
                };
                if(timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(task, 10000, 5000);


            }

        }
        if ("play".equals(command)) {
            String videoId = request.getParameter("id");
            String subtitleLanguage = "en";
            boolean subtitleMatch = false;
            Video video = VideoRegistry.getInstance().getVideo(videoId);

            if (video != null) {

                //TODO check if movie is already remuxed, skip the following steps if so.


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


                StreamAnalyzer analyzer = new StreamAnalyzer();
                MediaInfo mediaInfo = analyzer.analyze(video);


                //TODO move this code to some sort StreamMuxer factory
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



//                if (!DeviceRegistry.getInstance().getDevices().isEmpty()) {
//
//                    PlayCommand cmd = new PlayCommand("http://192.168.1.13:8085/files/rob.mp4", 0);
//                    DeviceConnection conn = new DeviceConnection((Device) DeviceRegistry.getInstance().getDevices().toArray()[0]);
//                    DeviceResponse tvresponse = conn.sendCommand(cmd);
//
//                    LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());
//                }
            }

        }
        if ("stop".equals(command)) {
            String videoId = request.getParameter("id");
            Video video = VideoRegistry.getInstance().getVideo(videoId);
            if (video != null) {
                if (!DeviceRegistry.getInstance().getDevices().isEmpty()) {

                    StopCommand cmd = new StopCommand();
                    DeviceConnection conn = new DeviceConnection((Device) DeviceRegistry.getInstance().getDevices().toArray()[0]);
                    DeviceResponse tvresponse = conn.sendCommand(cmd);

                    LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());
                }
            }
        }

        if ("info".equals(command)) {
            if (!DeviceRegistry.getInstance().getDevices().isEmpty()) {

                PlayBackInfoCommand cmd = new PlayBackInfoCommand();
                DeviceConnection conn = new DeviceConnection((Device) DeviceRegistry.getInstance().getDevices().toArray()[0]);
                DeviceResponse tvresponse = conn.sendCommand(cmd);

                LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());
                LOGGER.info("content: \n" + tvresponse.getContent());

                ErrorLogCommand errorLogCommand = new ErrorLogCommand();

                DeviceResponse tvresponse2 = conn.sendCommand(errorLogCommand);

                LOGGER.info("response: " + tvresponse2.getResponseCode() + " " + tvresponse2.getResponseMessage());
                LOGGER.info("content: \n" + tvresponse2.getContent());
                File file = new File("/Users/jasper/Documents/movie_tmp/error.plist");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(tvresponse2.getContent().getBytes());
                    fos.close();
                } catch (Exception ex) {
                    LOGGER.error("error writing file", ex);
                }

            }
        }
        if ("scrub".equals(command)) {
            if (!DeviceRegistry.getInstance().getDevices().isEmpty()) {

                ScrubCommand cmd = new ScrubCommand();
                DeviceConnection conn = new DeviceConnection((Device) DeviceRegistry.getInstance().getDevices().toArray()[0]);
                DeviceResponse tvresponse = conn.sendCommand(cmd);

                LOGGER.info("response: " + tvresponse.getResponseCode() + " " + tvresponse.getResponseMessage());
                LOGGER.info("content: \n" + tvresponse.getContent());
            }
        }
    }
}
