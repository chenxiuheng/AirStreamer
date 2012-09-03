/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.Device;
import repsaj.airstreamer.server.Service;

/**
 *
 * @author jasper
 */
public class WebService implements Service {

    private static final Logger LOGGER = Logger.getLogger(Device.class);
    private Tomcat tomcat = null;

    @Override
    public void init() {
        tomcat = new Tomcat();
        tomcat.setPort(8085);

        Context ctx = tomcat.addContext("/files", new File(".").getAbsolutePath());
        ctx.addMimeMapping("m3u8", "application/x-mpegURL");
        ctx.addMimeMapping("ts", "video/MP2T");
        ctx.addMimeMapping("mov", "video/quicktime");
        ctx.addMimeMapping("mp3", "audio/MPEG3");
        ctx.addMimeMapping("aac", "audio/aac");
        ctx.addMimeMapping("m4a", "audio/mpeg4");
        ctx.addMimeMapping("m4v", "video/mpeg4");
        ctx.addMimeMapping("mp4", "video/mp4");
        ctx.addMimeMapping("html", "text/html");
        
        Tomcat.addServlet(ctx, "file", new FileServlet("/Users/jasper/Documents/movie_tmp/"));
        ctx.addServletMapping("/*", "file");
    }

    @Override
    public void start() {
        try {
            tomcat.start();
        } catch (Exception ex) {
            LOGGER.error("error starting webserver", ex);
        }
    }

    @Override
    public void stop() {
        try {
            tomcat.stop();
        } catch (Exception ex) {
            LOGGER.error("error stopping webserver", ex);
        }
    }
}
