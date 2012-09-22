/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import org.junit.Test;

/**
 *
 * @author jasper
 */
public class DirectoryIndexerTest {

    @Test
    public void testMovieIndexer() {
        String path = this.getClass().getResource("/directory_indexer/movies/").getPath();
        System.out.println(path);
        
        MovieDirectoryIndexer indexer = new MovieDirectoryIndexer();
        indexer.indexDirectory(path);

    }
}