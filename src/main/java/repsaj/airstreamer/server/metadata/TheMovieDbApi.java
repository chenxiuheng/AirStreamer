/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import com.moviejukebox.themoviedb.TheMovieDb;
import com.moviejukebox.themoviedb.model.MovieDb;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Movie;

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
            } catch (IOException ex) {
                LOGGER.error("Error connecting to TheMovieDb", ex);
            }
        }
    }

    public void updateMovie(Movie movie) {
        initApi();

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
                        doUpdateMovie(movie, tmpMovie.getId());
                        break;
                    }
                }
            } else {
                //just pick the first one
                LOGGER.info("Using first result " + movies.get(0).getTitle());
                doUpdateMovie(movie, movies.get(0).getId());
            }
        }
    }

    private void doUpdateMovie(Movie movie, int moveiId) {

        MovieDb movieDb = theMovieDb.getMovieInfo(moveiId, DEF_LANGUAGE);
        if (movieDb != null) {
            LOGGER.info("Udating movie...");
            movie.setMovieId(movieDb.getId());
            movie.setName(movieDb.getTitle());
            movie.setDescription(movieDb.getOverview());
        } else {
        }

    }
}
