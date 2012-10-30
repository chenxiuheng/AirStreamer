/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import repsaj.airstreamer.server.Service;
import repsaj.airstreamer.server.model.Resource;
import repsaj.airstreamer.server.model.Subtitle;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public class ResourceManager extends Service {

    private static ResourceManager INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class);
    private Executor executor = Executors.newFixedThreadPool(2);

    public static ResourceManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourceManager();
        }

        return INSTANCE;
    }

    private ResourceManager() {
    }

    public void download(Resource resource, String url, Video video) {
        String targetPath = getApplicationSettings().getResourcePath();
        ResourceDownloadTask downloadTask = new ResourceDownloadTask(resource, url, targetPath, video);
        executor.execute(downloadTask);
    }

    private synchronized void updateVideo(Resource resource, String videoId) {

        Video tmpVid = getDatabase().getVideoById(videoId);
        tmpVid.getResources().put(resource.getType(), resource);
        getDatabase().save(tmpVid);

    }

    public void download(Subtitle subtitle, String url, Video video, boolean async) {
        SubtitleDownloadTask downloadTask = new SubtitleDownloadTask(subtitle, url, video);

        if (async) {
            executor.execute(downloadTask);
        } else {
            try {
                Thread task = new Thread(downloadTask);
                task.start();
                task.join();
            } catch (InterruptedException ex) {
            }
        }
    }

    private synchronized void updateVideo(Subtitle subtitle, String videoId) {
        Video tmpVid = getDatabase().getVideoById(videoId);
        tmpVid.getSubtitles().add(subtitle);
        getDatabase().save(tmpVid);
    }

    private class ResourceDownloadTask implements Runnable {

        private Resource resource;
        private String url;
        private String targetPath;
        private Video video;

        public ResourceDownloadTask(Resource resource, String url, String targetPath, Video video) {
            this.resource = resource;
            this.url = url;
            this.targetPath = targetPath;
            this.video = video;
        }

        @Override
        public void run() {
            try {
                //Download file
                LOGGER.info("Starting task to donwload " + url);
                LOGGER.info("saving file to: " + targetPath);
                File destFile = new File(targetPath + resource.getPath());
                URL downloadUrl = new URL(url);
                FileUtils.copyURLToFile(downloadUrl, destFile);
                LOGGER.info("Download completed");
                updateVideo(resource, video.getId());

                if ("poster".equalsIgnoreCase(resource.getType())) {
                    LOGGER.info("Creating thumbnail for poster");
                    //Create thumbnail
                    String resourceType = resource.getType() + "_thumb";
                    String path = "/" + video.getId() + "/" + resourceType + ".jpg";
                    Resource thumbnailResource = new Resource(resourceType, path);

                    BufferedImage img = ImageIO.read(destFile);
                    BufferedImage thumbnail = Scalr.resize(img, Scalr.Method.QUALITY, 300, Scalr.OP_ANTIALIAS);
                    File output = new File(targetPath + path);
                    ImageIO.write(thumbnail, "JPG", output);

                    updateVideo(thumbnailResource, video.getId());
                }

            } catch (IOException ex) {
                LOGGER.error("error downloading resource", ex);
            }

        }
    }

    private class SubtitleDownloadTask implements Runnable {

        private Subtitle subtitle;
        private String url;
        private Video video;

        public SubtitleDownloadTask(Subtitle subtitle, String url, Video video) {
            this.subtitle = subtitle;
            this.url = url;
            this.video = video;
        }

        @Override
        public void run() {
            try {
                //Download file
                LOGGER.info("Starting task to donwload " + url);
                LOGGER.info("saving file to: " + subtitle.getPath());
                File destFile = new File(subtitle.getPath());
                URL downloadUrl = new URL(url);
                FileUtils.copyURLToFile(downloadUrl, destFile);
                LOGGER.info("Download completed");
                updateVideo(subtitle, video.getId());

            } catch (IOException ex) {
                LOGGER.error("error downloading resource", ex);
            }

        }
    }
}
