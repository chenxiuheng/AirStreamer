/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.io.File;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.Service;
import repsaj.airstreamer.server.db.Database;
import repsaj.airstreamer.server.model.Movie;
import repsaj.airstreamer.server.model.Subtitle;
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
    private TheMovieDbApi movieDbApi;

    @Override
    public void init() {
        tvDbApi = new TheTvDbApi(getApplicationSettings().getResourcePath());
        movieDbApi = new TheMovieDbApi(getApplicationSettings().getResourcePath());
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    public void update() {
        checkVideos();
        indexSeries(getApplicationSettings().getTvshowsPath());
        updateSeries();
        indexEpisodes();
        updateEpisodes();
        indexMovies(getApplicationSettings().getMoviePath());
        updateMovies();
        updateSubtitles(getApplicationSettings().getMoviePath());
        updateSubtitles(getApplicationSettings().getTvshowsPath());
    }

    private void checkVideos() {
        Database db = getDatabase();

        List<Video> videos = db.getVideosByType(VideoTypeFactory.MOVIE_TYPE);
        checkVideos(videos);

        videos = db.getVideosByType(VideoTypeFactory.EPISODE_TYPE);
        checkVideos(videos);

        videos = db.getVideosByType(VideoTypeFactory.SERIE_TYPE);
        checkVideos(videos);
    }

    private void checkVideos(List<Video> videos) {
        Database db = getDatabase();

        for (Video video : videos) {
            String path = video.getPath();
            File tmpFile = new File(path);
            if (!tmpFile.exists()) {
                LOGGER.info("Removing video: " + video.getName());
                db.remove(video);
            }
        }
    }

    private void indexSeries(String path) {
        Database db = getDatabase();
        TvShowDirectoryIndexer indexer = new TvShowDirectoryIndexer();
        List<TvShowSerie> shows = indexer.indexTvShows(path);

        for (TvShowSerie serie : shows) {
            Video vid = db.getVideoByPath(serie.getPath());
            if (vid == null) {
                LOGGER.info("Adding tv serie: " + serie.getName());
                serie.setAdded(new Date());
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
                if (serie.getShowId() == null || serie.getDescription() == null) {
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
                        episode.setAdded(new Date());
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

    private void indexMovies(String path) {
        Database db = getDatabase();
        MovieDirectoryIndexer indexer = new MovieDirectoryIndexer();

        List<Movie> movies = indexer.indexDirectory(path);
        for (Movie movie : movies) {
            Video vid = db.getVideoByPath(movie.getPath());
            if (vid == null) {
                LOGGER.info("Adding movie: " + movie.getName());
                movie.setAdded(new Date());
                db.save(movie);
            }
        }
    }

    private void updateMovies() {
        Database db = getDatabase();
        List<Video> movies = db.getVideosByType(VideoTypeFactory.MOVIE_TYPE);

        for (Video video : movies) {
            if (video instanceof Movie) {
                Movie movie = (Movie) video;

                if (movie.getMovieId() <= 0 || movie.getDescription() == null) {
                    movieDbApi.updateMovie(movie);
                    db.save(movie);
                }
            }
        }
    }

    private void updateSubtitles(String dir) {
        Database db = getDatabase();

        SubtitleDirectoryIndexer indexer = new SubtitleDirectoryIndexer();
        List<Subtitle> subtitles = indexer.indexSubtitles(dir);

        for (Subtitle sub : subtitles) {
            String path = FilenameUtils.removeExtension(sub.getPath());

            if (!sub.getLanguage().equalsIgnoreCase("def")) {
                int index = path.indexOf(sub.getLanguage());
                path = path.substring(0, index - 1);
            }

            Video vid = db.searchVideoByPath(path);
            if (vid != null) {
                LOGGER.info("Found match!: " + path);
                boolean alreadyExists = false;
                for (Subtitle tmp : vid.getSubtitles()) {
                    if (tmp.getPath().equalsIgnoreCase(sub.getPath())) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (!alreadyExists) {
                    LOGGER.info("Adding subtitle to video " + vid.getName());
                    vid.getSubtitles().add(sub);
                    db.save(vid);
                } else {
                    LOGGER.info("Subtitle already present");
                }
            } else {
                LOGGER.info("Unable to find video");
            }
        }

    }
}
