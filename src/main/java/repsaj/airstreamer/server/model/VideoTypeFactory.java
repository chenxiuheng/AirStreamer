/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

/**
 *
 * @author jasper
 */
public class VideoTypeFactory {

    public static String MOVIE_TYPE = "movie";
    public static String EPISODE_TYPE = "episode";
    public static String SERIE_TYPE = "serie";

    public static Video videoForType(String type) {

        if (MOVIE_TYPE.equals(type)) {
            return new Movie();
        } else if (EPISODE_TYPE.equals(type)) {
            return new TvShowEpisode();
        } else if (SERIE_TYPE.equals(type)) {
            return new TvShowSerie();
        } else {
            throw new UnsupportedOperationException("type not supported");
        }

    }
}
