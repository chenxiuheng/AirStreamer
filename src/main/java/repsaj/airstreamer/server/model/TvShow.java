/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repsaj.airstreamer.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jasper
 */
public class TvShow {
    private String name;
    private int showId;
    private List<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the showId
     */
    public int getShowId() {
        return showId;
    }

    /**
     * @param showId the showId to set
     */
    public void setShowId(int showId) {
        this.showId = showId;
    }

    /**
     * @return the episodes
     */
    public List<TvShowEpisode> getEpisodes() {
        return episodes;
    }

    /**
     * @param episodes the episodes to set
     */
    public void setEpisodes(List<TvShowEpisode> episodes) {
        this.episodes = episodes;
    }
}
