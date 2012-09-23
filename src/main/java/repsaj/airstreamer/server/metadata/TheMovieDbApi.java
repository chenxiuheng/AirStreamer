/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import com.moviejukebox.themoviedb.TheMovieDb;
import com.moviejukebox.themoviedb.model.MovieDb;
import java.io.IOException;
import java.util.List;
import repsaj.airstreamer.server.model.Movie;

/**
 *
 * @author jasper
 */
public class TheMovieDbApi {

    private static final String API_KEY = "TODO";
    private static final String DEF_LANGUAGE = "en";
    private TheMovieDb theMovieDb;

    public TheMovieDbApi() {
        try {
            theMovieDb = new TheMovieDb(API_KEY);
        } catch (IOException ex) {
        }
    }

    public void updateMovie(Movie movie) {
        List<MovieDb> movies = theMovieDb.searchMovie(movie.getName(), DEF_LANGUAGE, false);
        if(!movies.isEmpty()){
            if(movie.getYear()>0){
                //search movie by year
                for(MovieDb tmpMovie : movies){
                    //parse release date
                    Integer releaseYear = Integer.valueOf(tmpMovie.getReleaseDate().substring(0, 4));
                    if(movie.getYear() == releaseYear){
                        doUpdateMovie(movie, tmpMovie);
                        break;
                    }
                }
            }
            else{
                //just pick the first one
                doUpdateMovie(movie, movies.get(0));
            }
        }
    }
    
    private void doUpdateMovie(Movie movie, MovieDb movieDb){
        movie.setMovieId(movieDb.getId());
        movie.setName(movieDb.getTitle());
        movie.setDescription(movieDb.getOverview());
        
    }
}
