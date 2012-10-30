package repsaj.airstreamer.server.model;

import java.util.Map;

/**
 *
 * @author jasper
 */
public class Movie extends Video {

    private int year;
    private int movieId;
    private int length;
    private double rating;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("year", year);
        map.put("movieId", movieId);
        map.put("length", length);
        map.put("rating", rating);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);
        year = (Integer) map.get("year");
        movieId = (Integer) map.get("movieId");
        length = (Integer) map.get("length");
        rating = (Double) map.get("rating");
    }

    @Override
    public String getType() {
        return "movie";
    }
}
