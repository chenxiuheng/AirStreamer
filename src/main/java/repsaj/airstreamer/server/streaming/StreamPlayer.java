/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.util.List;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Session;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public abstract class StreamPlayer {

    private static final Logger LOGGER = Logger.getLogger(StreamPlayer.class);
    protected String tmpPath;
    protected Session session;
    protected Video video;
    protected MediaInfo mediaInfo;

    public StreamPlayer(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public void play() {

        StreamAnalyzer analyzer = new StreamAnalyzer(tmpPath);
        mediaInfo = analyzer.analyze(video);

        doPrepare();
        doPlay();
    }

    public void stop() {
        doStop();
    }

    abstract protected List<StreamInfo> doPrepare();

    abstract protected void doPlay();

    abstract protected void doStop();

    /**
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * @return the video
     */
    public Video getVideo() {
        return video;
    }

    /**
     * @param video the video to set
     */
    public void setVideo(Video video) {
        this.video = video;
    }
}
