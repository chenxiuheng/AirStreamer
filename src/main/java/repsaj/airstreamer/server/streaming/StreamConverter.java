/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public abstract class StreamConverter {

    protected Video video;
    private String filesPath;
    protected StreamInfo streamInfo;
    protected String toCodec;
    protected int segmentTime = 10;
    protected String outputPath;
    protected List<JobAttachment> jobAttachments = new ArrayList<JobAttachment>();

    public void convertStream(Video video, StreamInfo streamInfo, String toCodec) {
        if (filesPath == null) {
            throw new IllegalStateException("filesPath not set!");
        }
        this.video = video;
        this.streamInfo = streamInfo;
        this.toCodec = toCodec;
        determineOutputPath();
        ensureOutputPathExists();
        doConvert();
    }

    abstract protected void doConvert();

    protected void determineOutputPath() {

        String tmpPath = filesPath + "video/";
        tmpPath += video.getId() + "/";

        String codec = toCodec != null ? toCodec : streamInfo.getCodec();
        tmpPath += streamInfo.getMediaType() + "_" + codec;
        if (streamInfo.getLanguage() != null) {
            tmpPath += "_" + streamInfo.getLanguage();
        }
        tmpPath += "/";

        this.outputPath = tmpPath;
    }

    protected void ensureOutputPathExists() {
        //ensure output dir exists
        File dir = new File(outputPath);
        //LOGGER.info("creating directory " + dir.getPath());
        dir.mkdirs();
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public void addAttachment(JobAttachment attachment) {
        jobAttachments.add(attachment);
    }

    public void stop() {
    }

    /**
     * @return the video
     */
    public Video getVideo() {
        return video;
    }

    /**
     * @return the filesPath
     */
    public String getFilesPath() {
        return filesPath;
    }

    /**
     * @return the streamInfo
     */
    public StreamInfo getStreamInfo() {
        return streamInfo;
    }

    /**
     * @return the toCodec
     */
    public String getToCodec() {
        return toCodec;
    }

    /**
     * @return the segmentTime
     */
    public int getSegmentTime() {
        return segmentTime;
    }

    /**
     * @return the outputPath
     */
    public String getOutputPath() {
        return outputPath;
    }
}
