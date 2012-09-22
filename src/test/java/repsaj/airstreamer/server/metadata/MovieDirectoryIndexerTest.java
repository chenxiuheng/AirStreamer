/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import repsaj.airstreamer.server.model.Movie;

/**
 *
 * @author jasper
 */
public class MovieDirectoryIndexerTest {

    @Test
    public void testMovieIndexer() {
        String path = this.getClass().getResource("/directory_indexer/movies/").getPath();
        System.out.println(path);
        
        MovieDirectoryIndexer indexer = new MovieDirectoryIndexer();
        List<Movie> movies = indexer.indexDirectory(path);

        Assert.assertEquals(8, movies.size());

    }
}