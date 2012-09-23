/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repsaj.airstreamer.server.db;

import java.util.List;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public interface Database{

    Video getVideoById(String id);

    Video getVideoByPath(String path);

    List<Video> getVideosByType(String type);

    void save(Video video);

    void remove(Video video);

}
