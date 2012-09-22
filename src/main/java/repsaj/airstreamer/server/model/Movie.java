/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repsaj.airstreamer.server.model;

/**
 *
 * @author jasper
 */
public class Movie extends Video{
    private int year;

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }
}
