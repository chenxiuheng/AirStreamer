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
public class TvShowSerie extends Video{

    private String showId;


    /**
     * @return the showId
     */
    public String getShowId() {
        return showId;
    }

    /**
     * @param showId the showId to set
     */
    public void setShowId(String showId) {
        this.showId = showId;
    }

   

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("showId", showId);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);
        showId = (String) map.get("showId");
    }

    @Override
    public String getType() {
        return "serie";
    }

}
