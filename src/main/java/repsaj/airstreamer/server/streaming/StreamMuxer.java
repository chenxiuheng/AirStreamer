/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public abstract class StreamMuxer {

    protected Video video;
    protected String filesPath;
    protected StreamInfo streamInfo;
    protected String toCodec;

    public void muxStream(Video video, StreamInfo streamInfo, String toCodec) {
        this.video = video;
        this.streamInfo = streamInfo;
        this.toCodec = toCodec;
        doMux();
    }

    abstract protected void doMux();

    public void stop() {
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

    /**
     * @return the filesPath
     */
    public String getFilesPath() {
        return filesPath;
    }

    /**
     * @param filesPath the filesPath to set
     */
    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    /**
     * @return the streamInfo
     */
    public StreamInfo getStreamInfo() {
        return streamInfo;
    }

    /**
     * @param streamInfo the streamInfo to set
     */
    public void setStreamInfo(StreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    /**
     * @return the toCodec
     */
    public String getToCodec() {
        return toCodec;
    }

    /**
     * @param toCodec the toCodec to set
     */
    public void setToCodec(String toCodec) {
        this.toCodec = toCodec;
    }
}
