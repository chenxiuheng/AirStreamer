/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Episode;
import com.moviejukebox.thetvdb.model.Series;
import java.util.List;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Resource;
import repsaj.airstreamer.server.model.TvShowEpisode;
import repsaj.airstreamer.server.model.TvShowSerie;

/**
 *
 * @author jasper
 */
public class TheTvDbApi {

    private static final Logger LOGGER = Logger.getLogger(TheTvDbApi.class);
    private static final String API_KEY = "8C9A22021FE0D96F";
    private static final String DEF_LANGUAGE = "en";
    private TheTVDB theTVDB;
    private String resourcePath;

    public TheTvDbApi(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    private void initApi() {
        if (theTVDB == null) {
            theTVDB = new TheTVDB(API_KEY);
        }
    }

    public void updateSerie(TvShowSerie serie) {
        initApi();

        if (serie.getShowId() == null) {
            LOGGER.info("Searching thetvdb for: " + serie.getName());
            List<Series> series = theTVDB.searchSeries(serie.getName(), DEF_LANGUAGE);
            LOGGER.info("Search returned with " + series.size() + " results");
            if (!series.isEmpty()) {
                //for now pick the first one
                Series tmpSerie = series.get(0);
                LOGGER.info("Updating serie.. " + tmpSerie.getSeriesName());
                serie.setShowId(tmpSerie.getId());
                serie.setName(tmpSerie.getSeriesName());

            }
        }

        Series detailSerieInfo = theTVDB.getSeries(serie.getShowId(), DEF_LANGUAGE);
        if (detailSerieInfo != null) {
            LOGGER.info("Updating " + detailSerieInfo.getSeriesName());
            serie.setDescription(detailSerieInfo.getOverview());
            if (detailSerieInfo.getBanner() != null && !detailSerieInfo.getBanner().isEmpty()) {
                Resource banner = new Resource("banner", "/" + serie.getId() + "/banner.jpg");
                ResourceDownloader.INSTANCE.download(banner, detailSerieInfo.getBanner(), resourcePath);
                serie.getResources().put(banner.getType(), banner);
            }
            if (detailSerieInfo.getPoster() != null && !detailSerieInfo.getPoster().isEmpty()) {
                Resource poster = new Resource("poster", "/" + serie.getId() + "/poster.jpg");
                ResourceDownloader.INSTANCE.download(poster, detailSerieInfo.getPoster(), resourcePath);
                serie.getResources().put(poster.getType(), poster);
            }
        }
    }

    public void updateEpisode(TvShowSerie serie, TvShowEpisode episode) {
        initApi();

        if (serie.getShowId() == null) {
            LOGGER.warn("serie id unknown, can't continue");
            return;
        }

        Episode tmpEpisode = theTVDB.getEpisode(serie.getShowId(), episode.getSeason(), episode.getEpisode(), DEF_LANGUAGE);
        if (tmpEpisode != null) {
            LOGGER.info("Updating episode Serie: " + serie.getName() + " Season: " + episode.getSeason() + " Episode: " + episode.getEpisode());

            episode.setEpisodeId(tmpEpisode.getId());
            episode.setName(tmpEpisode.getEpisodeName());
            episode.setDescription(tmpEpisode.getOverview());

            Resource poster = new Resource("poster", "/" + episode.getId() + "/poster.jpg");
            ResourceDownloader.INSTANCE.download(poster, tmpEpisode.getFilename(), resourcePath);
            episode.getResources().put(poster.getType(), poster);
        } else {
            LOGGER.warn("No episode found for " + serie.getName()
                    + " Season: " + episode.getSeason() + " Episode: " + episode.getEpisode());
        }


    }
}
