/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import repsaj.airstreamer.server.model.TvShowEpisode;
import repsaj.airstreamer.server.model.TvShowSerie;

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
        List<TvShowSerie> shows = indexer.indexTvShows(path);
         Assert.assertEquals(4, shows.size());

        for(TvShowSerie serie: shows) {
            List<TvShowEpisode> episodes = indexer.indexTvShow(serie);
        }

       
    }
}
