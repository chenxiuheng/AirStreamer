/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public class VideoRegistry {

    private static final Logger LOGGER = Logger.getLogger(VideoRegistry.class);
    private Map<String, Video> videos = new HashMap<String, Video>();
    private static VideoRegistry INSTANCE = null;

    private VideoRegistry() {
    }

    public static VideoRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VideoRegistry();
        }
        return INSTANCE;
    }

    public void addVideo(Video video) {
        LOGGER.info("addVideo " + video.getId());
        videos.put(video.getId(), video);
    }

    public void removeVideo(String id) {
        LOGGER.info("removeDevice " + id);
        videos.remove(id);
    }

    public Video getVideo(String id) {
        return videos.get(id);
    }

    public Collection<Video> getVideos() {
        return videos.values();
    }
}
