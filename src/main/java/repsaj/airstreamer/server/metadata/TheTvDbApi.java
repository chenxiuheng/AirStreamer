/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Episode;
import com.moviejukebox.thetvdb.model.Series;
import java.util.List;
import repsaj.airstreamer.server.model.TvShowEpisode;
import repsaj.airstreamer.server.model.TvShowSerie;

/**
 *
 * @author jasper
 */
public class TheTvDbApi {

    private static final String API_KEY = "8C9A22021FE0D96F";
    private static final String DEF_LANGUAGE = "en";
    private TheTVDB theTVDB = new TheTVDB(API_KEY);

    public void updateSerie(TvShowSerie serie) {
        if (serie.getShowId() == null) {
            List<Series> series = theTVDB.searchSeries(serie.getName(), DEF_LANGUAGE);
            if (!series.isEmpty()) {
                //for now pick the first one
                Series tmpSerie = series.get(0);

                serie.setShowId(tmpSerie.getId());
                serie.setName(tmpSerie.getSeriesName());
            }
        }
    }

    public void updateEpisode(TvShowSerie serie, TvShowEpisode episode) {
        if (serie.getShowId() == null) {
            //serie id unknown, can't continue
            return;
        }
        if (episode.getEpisodeId() != null) {
            //episode already indexed
            return;
        }

        Episode tmpEpisode = theTVDB.getEpisode(serie.getShowId(), episode.getSeason(), episode.getEpisode(), DEF_LANGUAGE);
        if (tmpEpisode != null) {
            episode.setName(tmpEpisode.getEpisodeName());
            episode.setEpisodeId(tmpEpisode.getId());
        }


    }
}
