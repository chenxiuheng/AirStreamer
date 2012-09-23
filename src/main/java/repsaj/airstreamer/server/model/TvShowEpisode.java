/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author jasper
 */
public class TvShowEpisode extends Video {

    private int season;
    private int episode;
    private Date airDate;
    private String episodeId;
    private String serieId;

    public TvShowEpisode() {
        super();
    }

    public TvShowEpisode(String serieId, int season, int episode) {
        super();
        this.serieId = serieId;
        this.season = season;
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

    /**
     * @return the episodeId
     */
    public String getEpisodeId() {
        return episodeId;
    }

    /**
     * @param episodeId the episodeId to set
     */
    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }


    /**
     * @return the serieId
     */
    public String getSerieId() {
        return serieId;
    }

    /**
     * @param serieId the serieId to set
     */
    public void setSerieId(String serieId) {
        this.serieId = serieId;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("season", season);
        map.put("episode", episode);
        map.put("airDate", airDate);
        map.put("episodeId", episodeId);
        map.put("serieId", serieId);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);
        season = (Integer) map.get("season");
        episode = (Integer) map.get("episode");
        airDate = (Date) map.get("airDate");
        episodeId = (String) map.get("episodeId");
        serieId = (String) map.get("serieId");
    }

    @Override
    public String getType() {
        return "episode";
    }



}
