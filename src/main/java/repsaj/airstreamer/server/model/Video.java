package repsaj.airstreamer.server.model;

import java.util.ArrayList;
import java.util.List;

public class Video {

    private String id;
    private String name;
    private String path;
    private List<Subtitle> subtitles = new ArrayList<Subtitle>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the subtitles
     */
    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    /**
     * @param subtitles the subtitles to set
     */
    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

}
