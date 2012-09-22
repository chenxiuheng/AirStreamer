/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import repsaj.airstreamer.server.model.TvShow;

/**
 *
 * @author jasper
 */
public class TvShowDirectoryIndexerTest {

    @Test
    public void testTvShowIndexer() {
        String path = this.getClass().getResource("/directory_indexer/tvshows/").getPath();
        System.out.println(path);

        TvShowDirectoryIndexer indexer = new TvShowDirectoryIndexer();
        List<TvShow> shows = indexer.indexDirectory(path);

        Assert.assertEquals(4, shows.size());
    }
}
