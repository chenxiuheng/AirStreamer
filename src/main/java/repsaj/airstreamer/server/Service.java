/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import repsaj.airstreamer.server.db.Database;

/**
 *
 * @author jasper
 */
public abstract class Service {

    private ApplicationSettings applicationSettings;
    private Database database;

    public void init() {
    }

    public void start() {
    }

    public void stop() {
    }

    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    public void setApplicationSettings(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}
