/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jasper
 */
public class Subtitle implements MapObject{
    private String language;
    private String path;
    private boolean external;

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the external
     */
    public boolean isExternal() {
        return external;
    }

    /**
     * @param external the external to set
     */
    public void setExternal(boolean external) {
        this.external = external;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("language", language);
        map.put("path", path);
        map.put("external", external);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        language = (String) map.get("language");
        path = (String) map.get("path");
        external = (Boolean) map.get("external");
    }
}
