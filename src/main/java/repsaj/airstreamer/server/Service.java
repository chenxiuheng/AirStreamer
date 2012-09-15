/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

/**
 *
 * @author jasper
 */
public abstract class Service {

    private ApplicationSettings applicationSettings;

    public abstract void init();

    public abstract void start();

    public abstract void stop();

    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    public void setApplicationSettings(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
    }
}
