/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.rest;

/**
 *
 * @author jasper
 */
public class RestTestObject {

    public String test() {
        return "test";
    }

    public String testWithId(String id) {
        return id;
    }

    public String testLatest(String param) {
        return param;
    }

    public int seasonsOfSeries(String serieId) {
        return 1;
    }

    public String episodesOfSeries(String serie, String season) {
        return serie + '-' + season;
    }
}
