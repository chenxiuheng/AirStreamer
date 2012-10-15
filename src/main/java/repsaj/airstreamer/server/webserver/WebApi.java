/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import repsaj.airstreamer.server.DeviceRegistry;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.model.TvShowEpisode;
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
                returnMessage = getSeries();
            } else if ("seasons".equals(command)) {
                String serieId = request.getParameter("serie");
                returnMessage = getSeasons(serieId);
            } else if ("episodes".equals(command)) {
                if (request.getParameter("id") != null) {
                    returnMessage = getEpisode(request.getParameter("id"));
                } else {
                    String serieId = request.getParameter("serie");
                    Integer season = Integer.valueOf(request.getParameter("season"));
                    returnMessage = getEpisodes(serieId, season);
                }
            } else if ("movies".equals(command)) {
                if (request.getParameter("id") != null) {
                    returnMessage = getMovieById(request.getParameter("id"));
                } else {
                    returnMessage = getMovies();
                }
            } else if ("devices".equals(command)) {
                returnMessage = getDevices();
            }


            if (returnMessage != null) {
                response.getWriter().print(returnMessage);
            }
        } catch (IOException ex) {
            LOGGER.error("Error handling request", ex);
        }
    }

    private String getSeries() throws IOException {
        List<Video> videos = db.getVideosByType(VideoTypeFactory.SERIE_TYPE);
        return writer.writeValueAsString(videos);
    }

    private String getSeasons(String serieId) throws IOException {
        HashSet<Integer> seasons = new HashSet<Integer>();
        //TODO move some of this code to the db class
        List<Video> videos = db.getVideosByType(VideoTypeFactory.EPISODE_TYPE);
        for (Video video : videos) {
            if (video instanceof TvShowEpisode) {
                TvShowEpisode episode = (TvShowEpisode) video;
                if (episode.getSerieId().equals(serieId)) {
                    seasons.add(episode.getSeason());
                }
            }
        }
        return writer.writeValueAsString(seasons);
    }

    private String getEpisodes(String serieId, int season) throws IOException {
        List<Video> videos = db.getEpisodes(serieId, season);
        return writer.writeValueAsString(videos);
    }

    private String getEpisode(String id) throws IOException {
        Video movie = db.getVideoById(id);
        return writer.writeValueAsString(movie);
    }

    private String getMovies() throws IOException {
        List<Video> videos = db.getVideosByType(VideoTypeFactory.MOVIE_TYPE);
        return writer.writeValueAsString(videos);
    }

    private String getMovieById(String id) throws IOException {
        Video movie = db.getVideoById(id);
        return writer.writeValueAsString(movie);
    }

    private String getDevices() throws IOException {
        List<Device> devices = DeviceRegistry.getInstance().getDevices();
        return writer.writeValueAsString(devices);
    }
}
