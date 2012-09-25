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
public class Resource implements MapObject{
    private String type;
    private String path;
    
    public Resource(){
    }
    
    public Resource(String type, String path){
        this.type = type;
        this.path = path;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("path", path);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        type = (String) map.get("type");
        path = (String) map.get("path");
    }
}
