/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.util.List;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.Service;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.model.TvShowEpisode;
import repsaj.airstreamer.server.model.TvShowSerie;
import repsaj.airstreamer.server.model.Video;
import repsaj.airstreamer.server.model.VideoTypeFactory;

/**
 *
 * @author jasper
 */
public class MetaDataUpdater extends Service {

    private static final Logger LOGGER = Logger.getLogger(MetaDataUpdater.class);
    private TheTvDbApi tvDbApi;

    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    public void update() {
        if (tvDbApi == null) {
            tvDbApi = new TheTvDbApi();
        }
//        indexSeries();
//        updateSeries();
//
//        indexEpisodes();
//        updateEpisodes();

    }

    private void indexSeries(String path) {
        Database db = getDatabase();
        TvShowDirectoryIndexer indexer = new TvShowDirectoryIndexer();
        List<TvShowSerie> shows = indexer.indexTvShows(path);

        for (TvShowSerie serie : shows) {
            Video vid = db.getVideoByPath(serie.getPath());
            if (vid == null) {
                LOGGER.info("Adding tv serie: " + serie.getName());
                db.save(serie);
            }
        }
    }

    private void updateSeries() {
        Database db = getDatabase();
        List<Video> shows = db.getVideosByType(VideoTypeFactory.SERIE_TYPE);

        for (Video video : shows) {
            if (video instanceof TvShowSerie) {
                TvShowSerie serie = (TvShowSerie) video;
                if (serie.getShowId() == null) {
                    tvDbApi.updateSerie(serie);
                    db.save(serie);
                }
            }
        }
    }

    private void indexEpisodes() {
        Database db = getDatabase();
        TvShowDirectoryIndexer indexer = new TvShowDirectoryIndexer();

        List<Video> series = db.getVideosByType(VideoTypeFactory.SERIE_TYPE);
        for (Video video : series) {
            if (video instanceof TvShowSerie) {
                TvShowSerie serie = (TvShowSerie) video;

                List<TvShowEpisode> episodes = indexer.indexTvShow(serie);
                for (TvShowEpisode episode : episodes) {
                    Video tmpepisode = db.getVideoByPath(episode.getPath());
                    if (tmpepisode == null) {
                        LOGGER.info("Adding tv episode " + episode.getName());
                        db.save(episode);
                    }
                }
            }
        }
    }

    private void updateEpisodes() {
        Database db = getDatabase();
        List<Video> episodes = db.getVideosByType(VideoTypeFactory.EPISODE_TYPE);

        for (Video video : episodes) {
            if (video instanceof TvShowEpisode) {
                TvShowEpisode episode = (TvShowEpisode) video;

                if (episode.getEpisodeId() == null) {

                    Video vserie = db.getVideoById(episode.getSerieId());
                    TvShowSerie serie = (TvShowSerie) vserie;

                    tvDbApi.updateEpisode(serie, episode);
                    db.save(episode);
                }
            }
        }
    }
}
