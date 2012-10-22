/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import java.util.List;
import repsaj.airstreamer.server.ApplicationSettings;
import repsaj.airstreamer.server.DeviceRegistry;
import repsaj.airstreamer.server.SessionRegistry;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.metadata.MetaDataUpdater;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.model.Session;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.streaming.AirPlayPlayer;
import repsaj.airstreamer.server.streaming.WebPlayer;

/**
 *
 * @author jasper
 */
public class Application {

    private Database db;
    private ApplicationSettings applicationSettings;

    public Application(Database db, ApplicationSettings applicationSettings) {
        this.db = db;
        this.applicationSettings = applicationSettings;
    }

    public List<Device> listDevices() {
        return DeviceRegistry.getInstance().getDevices();
    }

    public String playVideo(String videoId) {

        Video video = db.getVideoById(videoId);

        if (video != null) {
            Session session = new Session();

            WebPlayer player = new WebPlayer(applicationSettings.getTmpPath());
            player.setSession(session);
            player.setVideo(video);

            session.setPlayer(player);
            SessionRegistry.getInstance().addSession(session);

            player.play();

            return session.getSessionId();
        }
        return null;
    }

    public String play(String deviceId, String videoId) {

        Video video = db.getVideoById(videoId);

        if (video != null) {

            Device device = DeviceRegistry.getInstance().getDevice(deviceId);

            if (device != null) {

                Session session = new Session();
                session.setExternalDevice(device);

                AirPlayPlayer player = new AirPlayPlayer(applicationSettings);
                player.setSession(session);
                player.setVideo(video);

                session.setPlayer(player);
                SessionRegistry.getInstance().addSession(session);

                player.play();

                return session.getSessionId();
            }
        }

        return null;

    }

    public void stop(String sessionId) {
        Session session = SessionRegistry.getInstance().getSession(sessionId);
        if (session != null) {
            if (session.getPlayer() != null) {
                session.getPlayer().stop();
            }
        }
    }

    public void index() {
        MetaDataUpdater.getInstance().update();
    }
}
