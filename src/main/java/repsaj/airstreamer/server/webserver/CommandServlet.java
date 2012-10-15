/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.*;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.metadata.MetaDataUpdater;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.model.Session;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.streaming.*;

/**
 *
 * @author jasper
 */
public class CommandServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CommandServlet.class);
    private ApplicationSettings applicationSettings;
    private Database db;

    public CommandServlet(ApplicationSettings applicationSettings, Database db) {
        this.applicationSettings = applicationSettings;
        this.db = db;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String command = request.getParameter("command");

        if ("index".equals(command)) {
            MetaDataUpdater metaDataUpdater = (MetaDataUpdater) Main.serviceWrapper.getServiceByName("MetaDataUpdater");
            metaDataUpdater.update();
        }

        if ("play".equals(command)) {
            String videoId = request.getParameter("id");
            String deviceId = request.getParameter("device");
            Video video = db.getVideoById(videoId);

            if (video != null) {

                Device device = DeviceRegistry.getInstance().getDevice(deviceId);

                //for now only support devices
                if (device != null) {


                    Session session = new Session();
                    session.setExternalDevice(device);

                    AirPlayPlayer player = new AirPlayPlayer(applicationSettings.getTmpPath());
                    player.setSession(session);
                    player.setVideo(video);

                    session.setPlayer(player);
                    SessionRegistry.getInstance().addSession(session);

                    player.play();

                    try {
                        response.getWriter().write(session.getSessionId());
                    } catch (IOException ex) {
                        LOGGER.error("error writing to client", ex);
                    }
                }


            } else {
                throw new RuntimeException("Video not found");
            }


        }
        if ("stop".equals(command)) {

            String sessionId = request.getParameter("sid");
            Session session = SessionRegistry.getInstance().getSession(sessionId);
            StreamPlayer player = session.getPlayer();
            if (player != null) {
                player.stop();
            }

        }


    }
}
