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
import repsaj.airstreamer.server.DeviceRegistry;
import repsaj.airstreamer.server.SessionRegistry;
import repsaj.airstreamer.server.VideoRegistry;
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

 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String command = request.getParameter("command");
        
        
        if ("play".equals(command)) {
            String videoId = request.getParameter("id");
            Video video = VideoRegistry.getInstance().getVideo(videoId);
            
            if (video != null) {

                //for now only support devices
                if (!DeviceRegistry.getInstance().getDevices().isEmpty()) {
                    Device device = DeviceRegistry.getInstance().getDevices().get(0);
                    
                    Session session = new Session();
                    session.setExternalDevice(device);
                    
                    AirPlayPlayer player = new AirPlayPlayer();
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
