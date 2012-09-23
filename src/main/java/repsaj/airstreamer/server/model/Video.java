package repsaj.airstreamer.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Video implements MapObject {

    private String id;
    private String name;
    private String path;
    private String description;
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

    public abstract String getType();

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("name", name);
        map.put("path", path);
        map.put("description", description);
        map.put("type", getType());

        if (!subtitles.isEmpty()) {
            List<Map<String, Object>> tmpSubtitles = new ArrayList<Map<String, Object>>();
            for (Subtitle sub : subtitles) {
                tmpSubtitles.add(sub.toMap());
            }
            map.put("subtitles", tmpSubtitles);
        }

        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        id = (String) map.get("id");
        name = (String) map.get("name");
        path = (String) map.get("path");
        description = (String) map.get("description");

        List<Map<String, Object>> tmpSubtitles = (List<Map<String, Object>>) map.get("subtitles");
        if (tmpSubtitles != null) {
            subtitles.clear();
            for (Map<String, Object> submap : tmpSubtitles) {
                Subtitle sub = new Subtitle();
                sub.fromMap(submap);
                subtitles.add(sub);
            }
        }
    }
}
