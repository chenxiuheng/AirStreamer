/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

/**
 *
 * @author jasper
 */
public class StreamInfo implements Cloneable{

    public static final String H264 = "h264";
    public static final String AC3 = "ac3";
    public static final String AAC = "aac";
    public static final String SUBRIP = "subrip";
    public static final String WEBVVT = "vvt";
    private MediaType mediaType;
    private String codec;
    private String language;
    private int index;


    /**
     * @return the mediaType
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * @param mediaType the mediaType to set
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * @return the codec
     */
    public String getCodec() {
        return codec;
    }

    /**
     * @param codec the codec to set
     */
    public void setCodec(String codec) {
        this.codec = codec;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public enum MediaType {

        Video, Audio, Subtitle
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
