/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Resource;

/**
 *
 * @author jasper
 */
public enum ResourceDownloader {

    INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(ResourceDownloader.class);
    private Executor executor = Executors.newFixedThreadPool(2);

    private class DownloadTask implements Runnable {

        private Resource resource;
        private String url;
        private String targetPath;

        public DownloadTask(Resource resource, String url, String targetPath) {
            this.resource = resource;
            this.url = url;
            this.targetPath = targetPath;
        }

        @Override
        public void run() {
            try {
                LOGGER.info("Starting task to donwload " + url);
                File destFile = new File(targetPath + resource.getPath());
                URL downloadUrl = new URL(url);
                FileUtils.copyURLToFile(downloadUrl, destFile);
                LOGGER.info("Download completed");

            } catch (IOException ex) {
                LOGGER.error("error downloading resource", ex);
            }

        }
    }

    public void download(Resource resource, String url, String targetPath) {
        DownloadTask downloadTask = new DownloadTask(resource, url, targetPath);
        executor.execute(downloadTask);
    }
}
