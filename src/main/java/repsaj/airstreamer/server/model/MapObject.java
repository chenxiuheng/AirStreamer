/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

import java.util.Map;

/**
 *
 * @author jasper
 */
public interface MapObject {

    public Map<String, Object> toMap();

    public void fromMap(Map<String, Object> map);
}
