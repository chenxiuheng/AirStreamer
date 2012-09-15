/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.ApplicationSettings;
import repsaj.airstreamer.server.model.Device;
import repsaj.airstreamer.server.Service;

/**
 *
 * @author jasper
 */
public class WebService extends Service {

    private static final Logger LOGGER = Logger.getLogger(Device.class);
    private Tomcat tomcat = null;

    @Override
    public void init() {
        tomcat = new Tomcat();
        tomcat.setPort(8085);

        //Files
        Context ctxFiles = tomcat.addContext("/files", new File(".").getAbsolutePath());
        ctxFiles.addMimeMapping("m3u8", "application/x-mpegURL");
        ctxFiles.addMimeMapping("ts", "video/MP2T");
        ctxFiles.addMimeMapping("mov", "video/quicktime");
        ctxFiles.addMimeMapping("mp3", "audio/MPEG3");
        ctxFiles.addMimeMapping("aac", "audio/aac");
        ctxFiles.addMimeMapping("m4a", "audio/mpeg4");
        ctxFiles.addMimeMapping("m4v", "video/mpeg4");
        ctxFiles.addMimeMapping("mp4", "video/mp4");
        ctxFiles.addMimeMapping("html", "text/html");

        Tomcat.addServlet(ctxFiles, "file", new FileServlet(getApplicationSettings().getPath()));
        ctxFiles.addServletMapping("/*", "file");

        //Command
        Context ctxCommand = tomcat.addContext("/cmd", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctxCommand, "cmd", new CommandServlet(getApplicationSettings().getPath()));
        ctxCommand.addServletMapping("/*", "cmd");
        
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
