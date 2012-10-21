package repsaj.airstreamer.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Video implements MapObject {

    private String id;
    private String name;
    private String path;
    private String description;
    private Date added;
    private Double playbackPosition;
    private boolean skipIndex = false;
    private Map<String, Resource> resources = new HashMap<String, Resource>();
    private List<Subtitle> subtitles = new ArrayList<Subtitle>();

    public Video() {
        this.id = UUID.randomUUID().toString();
    }

    public Video(String id) {
        this.id = id;
    }

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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return the resources
     */
    public Map<String, Resource> getResources() {
        return resources;
    }

    /**
     * @param resources the resources to set
     */
    public void setResources(Map<String, Resource> resources) {
        this.resources = resources;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public Double getPlaybackPosition() {
        return playbackPosition;
    }

    public void setPlaybackPosition(Double playbackPosition) {
        this.playbackPosition = playbackPosition;
    }

    public boolean getSkipIndex() {
        return skipIndex;
    }

    public void setSkipIndex(boolean skipIndex) {
        this.skipIndex = skipIndex;
    }

    public abstract String getType();

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("name", name);
        map.put("path", path);
        map.put("description", description);
        map.put("type", getType());
        map.put("added", added);
        map.put("playbackPosition", playbackPosition);
        map.put("skipIndex", getSkipIndex());
        map.put("subtitles", MapObjectUtil.listToDbMap(subtitles));
        map.put("resources", MapObjectUtil.mapToDbMap(resources));

        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        id = (String) map.get("id");
        name = (String) map.get("name");
        path = (String) map.get("path");
        description = (String) map.get("description");
        added = (Date) map.get("added");
        playbackPosition = (Double) map.get("playbackPosition");
        if (map.get("skipIndex") != null) {
            skipIndex = (Boolean) map.get("skipIndex");
        }
        subtitles = MapObjectUtil.dbMapToList(map.get("subtitles"), Subtitle.class);
        resources = MapObjectUtil.dbMapToMap(map.get("resources"), Resource.class);

    }
}
