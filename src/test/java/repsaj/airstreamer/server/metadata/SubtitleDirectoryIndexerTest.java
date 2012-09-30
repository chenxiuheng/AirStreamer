/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import repsaj.airstreamer.server.model.Subtitle;

/**
 *
 * @author jasper
 */
public class SubtitleDirectoryIndexerTest {

    @Test
    public void testSubtitleIndexerMovie() {
        String path = this.getClass().getResource("/directory_indexer/movies/").getPath();
        System.out.println(path);

        SubtitleDirectoryIndexer indexer = new SubtitleDirectoryIndexer();
        List<Subtitle> subtitles = indexer.indexSubtitles(path);

        //TODO make usefull asserts
        Assert.assertEquals(1, subtitles.size());

    }

    @Test
    public void testSubtitleIndexerTvShows() {
        String path = this.getClass().getResource("/directory_indexer/tvshows/").getPath();
        System.out.println(path);

        SubtitleDirectoryIndexer indexer = new SubtitleDirectoryIndexer();
        List<Subtitle> subtitles = indexer.indexSubtitles(path);

        for (Subtitle sub : subtitles) {
            if (sub.getPath().contains("Breaking.Bad")) {
                Assert.assertEquals(true, sub.isExternal());
                Assert.assertEquals("def", sub.getLanguage());
            } else if (sub.getPath().contains("Californication")) {
                Assert.assertEquals(true, sub.isExternal());
                Assert.assertEquals("nl", sub.getLanguage());
            } else {
                Assert.fail("Subtitle not in unit test! " + sub.getPath());
            }

        }

    }
}
