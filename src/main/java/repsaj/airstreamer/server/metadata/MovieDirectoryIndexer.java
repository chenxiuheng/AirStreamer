/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.io.File;

/**
 *
 * @author jasper
 */
public class MovieDirectoryIndexer {

    public void indexDirectory(String path) {
        File movies_path = new File(path);

        File[] files = movies_path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("dir:" + file.getName());
                indexSubDirectory(file);
            } else {
                System.out.println("file:" + file.getName());
            }
        }


    }

    private void indexSubDirectory(File dir) {
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("dir [IGNORED]" + file.getName());
            } else {
                System.out.println("file:" + file.getName());
                
            }
        }
    }
}
