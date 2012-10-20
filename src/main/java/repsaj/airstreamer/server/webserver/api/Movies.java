/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import java.util.List;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.model.VideoTypeFactory;

/**
 *
 * @author jasper
 */
public class Movies {

    private Database db;

    public Movies(Database db) {
        this.db = db;
    }

    public List<Video> list() {
        return db.getVideosByType(VideoTypeFactory.MOVIE_TYPE);
    }

    public Video movie(String id) {
        return db.getVideoById(id);
    }
}
