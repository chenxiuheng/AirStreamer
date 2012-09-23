/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import repsaj.airstreamer.server.Service;

/**
 *
 * @author jasper
 */
public class MetaDataUpdater extends Service {

    private TheTvDbApi tvDbApi = new TheTvDbApi();

    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    public void update() {
        indexSeries();
        updateSeries();

        indexEpisodes();
        updateEpisodes();
        
    }

    private void indexSeries() {
    }

    private void updateSeries() {
    }

    private void indexEpisodes() {
    }

    private void updateEpisodes() {
    }
}
