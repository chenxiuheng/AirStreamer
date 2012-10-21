/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import com.moviejukebox.themoviedb.MovieDbException;
import com.moviejukebox.themoviedb.TheMovieDb;
import com.moviejukebox.themoviedb.model.MovieDb;
import java.util.List;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Movie;
import repsaj.airstreamer.server.model.Resource;

/**
 *
 * @author jasper
 */
public class TheMovieDbApi {

    private static final Logger LOGGER = Logger.getLogger(TheMovieDbApi.class);
    private static final String API_KEY = "b242079daf910ea9e8431c6e343e6da6";
    private static final String DEF_LANGUAGE = "en";
    private TheMovieDb theMovieDb;

    private void initApi() {
        if (theMovieDb == null) {
            try {
                theMovieDb = new TheMovieDb(API_KEY);
            } catch (MovieDbException ex) {
                LOGGER.error("Error connecting to TheMovieDb", ex);
            }
        }
    }

    public void updateMovie(Movie movie) {
        initApi();

        if (movie.getMovieId() <= 0 && movie.getSkipIndex() == false) {

            try {
                List<MovieDb> movies = theMovieDb.searchMovie(movie.getName(), DEF_LANGUAGE, false);
                LOGGER.info("Search for " + movie.getName() + " returned wih " + movies.size() + " results");

                if (!movies.isEmpty()) {
                    if (movie.getYear() > 0) {
                        //search movie by year
                        for (MovieDb tmpMovie : movies) {
                            //parse release date
                            Integer releaseYear = Integer.valueOf(tmpMovie.getReleaseDate().substring(0, 4));
                            if (movie.getYear() == releaseYear) {
                                LOGGER.info("Found movie with matching year " + tmpMovie.getTitle());
                                movie.setMovieId(tmpMovie.getId());
                                break;
                            }
                        }
                        //if no movie is found, use the first in the list
                        LOGGER.info("No movie with year found, using first result " + movies.get(0).getTitle());
                        movie.setMovieId(movies.get(0).getId());

                    } else {
                        //just pick the first one
                        LOGGER.info("Using first result " + movies.get(0).getTitle());
                        movie.setMovieId(movies.get(0).getId());
                    }
                } else {
                    movie.setSkipIndex(true);
                }
            } catch (MovieDbException ex) {
                LOGGER.error("Error searching theMovieDb", ex);
            }
        }

        doUpdateMovie(movie);
    }

    private void doUpdateMovie(Movie movie) {

        try {
            MovieDb movieDb = theMovieDb.getMovieInfo(movie.getMovieId(), DEF_LANGUAGE);

            if (movieDb != null && movieDb.getTitle() != null) {
                LOGGER.info("Udating movie... " + movieDb.getTitle());
                movie.setName(movieDb.getTitle());
                movie.setDescription(movieDb.getOverview());

                Integer releaseYear = Integer.valueOf(movieDb.getReleaseDate().substring(0, 4));
                movie.setYear(releaseYear);

                String baseUrl = theMovieDb.getConfiguration().getBaseUrl();
                //LOGGER.info("poster sizes: " + theMovieDb.getConfiguration().getPosterSizes());
                //LOGGER.info("backdrop sizes: " + theMovieDb.getConfiguration().getBackdropSizes());

                if (movieDb.getPosterPath() != null) {
                    Resource poster = new Resource("poster", "/" + movie.getId() + "/poster.jpg");
                    ResourceManager.getInstance().download(poster, baseUrl + "w500" + movieDb.getPosterPath(), movie);
                }

                if (movieDb.getBackdropPath() != null) {
                    Resource backdrop = new Resource("backdrop", "/" + movie.getId() + "/backdrop.jpg");
                    ResourceManager.getInstance().download(backdrop, baseUrl + "w1280" + movieDb.getBackdropPath(), movie);
                }
            }
        } catch (MovieDbException ex) {
            LOGGER.error("Error getting movie info from TheMovieDb", ex);
        }
    }
}
