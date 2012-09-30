/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import org.apache.catalina.Context;

/**
 *
 * @author jasper
 */
public class MimeMappingHelper {

    private MimeMappingHelper() {
    }

    public static void applyMapping(Context context) {

        //Video
        context.addMimeMapping("m3u8", "application/x-mpegURL");
        context.addMimeMapping("ts", "video/MP2T");
        context.addMimeMapping("mov", "video/quicktime");
        context.addMimeMapping("mp3", "audio/MPEG3");
        context.addMimeMapping("aac", "audio/aac");
        context.addMimeMapping("m4a", "audio/mpeg4");
        context.addMimeMapping("m4v", "video/mpeg4");
        context.addMimeMapping("mp4", "video/mp4");
        context.addMimeMapping("html", "text/html");

        //Images
        context.addMimeMapping("jpg", "image/jpeg");
        context.addMimeMapping("jpeg", "image/jpeg");
        context.addMimeMapping("png", "image/png");
        context.addMimeMapping("gif", "image/gif");

        //Text
        context.addMimeMapping("css", "text/css");
        context.addMimeMapping("html", "text/html");
        context.addMimeMapping("js", "text/javascript");
    }
}
