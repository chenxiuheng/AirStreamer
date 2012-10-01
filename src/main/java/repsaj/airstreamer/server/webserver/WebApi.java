/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.model.VideoTypeFactory;

/**
 *
 * @author jasper
 */
public class WebApi extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(WebApi.class);
    private ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
    private Database db;

    public WebApi(Database db) {
        this.db = db;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            String command = request.getParameter("command");

            String returnMessage = null;
            if ("series".equals(command)) {
                returnMessage = getSeries(request);
            }



            if (returnMessage != null) {
                response.getWriter().print(returnMessage);
            }
        } catch (IOException ex) {
            LOGGER.error("Error handling request", ex);
        }
    }

    private String getSeries(HttpServletRequest request) throws IOException {
        List<Video> videos = db.getVideosByType(VideoTypeFactory.SERIE_TYPE);
        return writer.writeValueAsString(videos);
    }
}
