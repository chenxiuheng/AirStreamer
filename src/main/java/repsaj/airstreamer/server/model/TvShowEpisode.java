/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

import java.util.Date;

/**
 *
 * @author jasper
 */
public class TvShowEpisode extends Video {

    private int season;
    private int episode;
    private Date airDate;

    public TvShowEpisode() {
    }

    public TvShowEpisode(int season, int episode) {
        this.season = episode;
        this.episode = episode;
    }

    /**
     * @return the season
     */
    public int getSeason() {
        return season;
    }

    /**
     * @param season the season to set
     */
    public void setSeason(int season) {
        this.season = season;
    }

    /**
     * @return the episode
     */
    public int getEpisode() {
        return episode;
    }

    /**
     * @param episode the episode to set
     */
    public void setEpisode(int episode) {
        this.episode = episode;
    }

    /**
     * @return the airDate
     */
    public Date getAirDate() {
        return airDate;
    }

    /**
     * @param airDate the airDate to set
     */
    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }
}
