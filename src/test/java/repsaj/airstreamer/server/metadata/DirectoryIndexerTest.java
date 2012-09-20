/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.io.File;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
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
        File movies_path = new File(path);
        Collection<File> files = FileUtils.listFiles(movies_path, null, true);
        for (File file : files) {

            String parent_dir = file.getParentFile().getName();
            if (!parent_dir.equals("movies")) {
                System.out.println("path: " + file.getParentFile().getName());
            }

            System.out.println("file: " + file.getName());
        }
    }
}
