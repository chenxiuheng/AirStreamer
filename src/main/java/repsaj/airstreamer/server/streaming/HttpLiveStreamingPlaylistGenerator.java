/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public class HttpLiveStreamingPlaylistGenerator implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(HttpLiveStreamingPlaylistGenerator.class);
    private static final String NEW_LINE = "\n";
    private final Thread directoryWatcher = new Thread(this);
    private boolean keepRunning = false;
    private final List<String> files = new ArrayList<String>();
    private final File directory;
    private final File playlistFile;

    public HttpLiveStreamingPlaylistGenerator(String path) {
        this.directory = new File(path);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("path must be a directory");
        }
        playlistFile = new File(path + "/index.m3u8");
    }

    public void start() {
        LOGGER.info("started PlayListGenerator in directory " + directory.getPath());
        generatePlayList(false);
        keepRunning = true;
        directoryWatcher.start();
    }

    public void finish() {
        keepRunning = false;
        directoryWatcher.interrupt();

        //generate final playlistfile
        generatePlayList(true);

        LOGGER.info("finished PlayListGenerator in directory " + directory.getPath());
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                Thread.sleep(2000);


                //Get all the files from the directory
                boolean fileListChanged = false;
                Collection<File> filesInDir = FileUtils.listFiles(directory, new String[]{"ts"}, false);
                synchronized (files) {
                    for (File file : filesInDir) {
                        if (!files.contains(file.getName())) {
                            files.add(file.getName());
                            fileListChanged = true;
                        }
                    }
                }

                if (fileListChanged) {
                    LOGGER.info("file list changed, generating new playlist file");
                    //Sort the files
                    synchronized (files) {
                        Collections.sort(files);
                    }
                    generatePlayList(false);
                }


            } catch (InterruptedException iex) {
                //ignore
            }
        }
    }

    private void generatePlayList(boolean isFinal) {
        StringBuilder builder = new StringBuilder();
        builder.append("#EXTM3U").append(NEW_LINE);
        builder.append("#EXT-X-PLAYLIST-TYPE:EVENT").append(NEW_LINE);
        builder.append("#EXT-X-TARGETDURATION:10").append(NEW_LINE);
        builder.append("#EXT-X-MEDIA-SEQUENCE:0").append(NEW_LINE);

        synchronized (files) {
            for (String file : files) {
                builder.append("#EXTINF:10,").append(NEW_LINE);
                builder.append(file).append(NEW_LINE);
            }
        }

        if (isFinal) {
            builder.append("#EXT-X-ENDLIST").append(NEW_LINE);
        }

        try {
            FileUtils.writeStringToFile(playlistFile, builder.toString());
        } catch (IOException ex) {
            LOGGER.error("error writing playlistfile", ex);
        }

    }
}
