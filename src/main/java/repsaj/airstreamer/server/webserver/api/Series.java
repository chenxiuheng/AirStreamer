/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.model.TvShowEpisode;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.model.VideoTypeFactory;

/**
 *
 * @author jasper
 */
public class Series {

    private Database db;

    public Series(Database db) {
        this.db = db;
    }

    public List<Video> list() {
        return db.getVideosByType(VideoTypeFactory.SERIE_TYPE);
    }

    public List<Integer> seasons(String serieId) {

        ArrayList<Integer> seasons = new ArrayList<Integer>();
        //TODO move some of this code to the db class
        List<Video> videos = db.getVideosByType(VideoTypeFactory.EPISODE_TYPE);
        for (Video video : videos) {
            if (video instanceof TvShowEpisode) {
                TvShowEpisode episode = (TvShowEpisode) video;
                if (episode.getSerieId().equals(serieId)) {
                    if (!seasons.contains(episode.getSeason())) {
                        seasons.add(episode.getSeason());
                    }
                }
            }
        }
        Collections.sort(seasons);
        return seasons;
    }

    public List<Video> episodes(String serieId, String season) {
        Integer seasonId = Integer.valueOf(season);
        List<Video> videos = db.getEpisodes(serieId, seasonId);
        return videos;
    }

    public Video episode(String id) {
        return db.getVideoById(id);
    }

    public List<Video> latestEpisodes(String max) {
        Integer maxRecords = Integer.valueOf(max);
        return db.getLatestVideo(maxRecords, VideoTypeFactory.EPISODE_TYPE);
    }
}
