/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public class Videos {

    private Database db;

    public Videos(Database db) {
        this.db = db;
    }

    public void watched(String videoId) {
        Video video = db.getVideoById(videoId);
        video.setWatched(true);
        db.save(video);
    }

    public void unwatched(String videoId) {
        Video video = db.getVideoById(videoId);
        video.setWatched(false);
        db.save(video);
    }
}
