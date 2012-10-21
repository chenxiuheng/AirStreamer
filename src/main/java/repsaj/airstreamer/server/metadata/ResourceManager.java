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
        DownloadTask downloadTask = new DownloadTask(resource, url, targetPath, video);
        executor.execute(downloadTask);
    }

    private synchronized void updateVideo(Resource resource, String videoId) {

        Video tmpVid = getDatabase().getVideoById(videoId);
        tmpVid.getResources().put(resource.getType(), resource);
        getDatabase().save(tmpVid);

    }

    private class DownloadTask implements Runnable {

        private Resource resource;
        private String url;
        private String targetPath;
        private Video video;

        public DownloadTask(Resource resource, String url, String targetPath, Video video) {
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
                File destFile = new File(targetPath + getResource().getPath());
                URL downloadUrl = new URL(url);
                FileUtils.copyURLToFile(downloadUrl, destFile);
                LOGGER.info("Download completed");
                updateVideo(resource, video.getId());

                LOGGER.info("Creating thumbnail");
                //Create thumbnail
                String resourceType = resource.getType() + "_thumb";
                String path = "/" + video.getId() + "/" + resourceType + ".jpg";
                Resource thumbnailResource = new Resource(resourceType, path);

                BufferedImage img = ImageIO.read(destFile);
                BufferedImage thumbnail = Scalr.resize(img, Scalr.Method.BALANCED, 300, Scalr.OP_ANTIALIAS);
                File output = new File(targetPath + path);
                ImageIO.write(thumbnail, "JPG", output);

                updateVideo(thumbnailResource, video.getId());

            } catch (IOException ex) {
                LOGGER.error("error downloading resource", ex);
            }

        }

        public Resource getResource() {
            return resource;
        }

        public Video getVideo() {
            return video;
        }
    }
}
