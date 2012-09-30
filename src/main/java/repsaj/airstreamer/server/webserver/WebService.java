/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;
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
        MimeMappingHelper.applyMapping(ctxFiles);
        Tomcat.addServlet(ctxFiles, "file", new FileServlet(getApplicationSettings().getTmpPath()));
        ctxFiles.addServletMapping("/*", "file");
        
        //Resources
        Context ctxResources = tomcat.addContext("/resources", new File(".").getAbsolutePath());
        MimeMappingHelper.applyMapping(ctxResources);
        Tomcat.addServlet(ctxResources, "resources", new FileServlet(getApplicationSettings().getResourcePath()));
        ctxResources.addServletMapping("/*", "resources");

        //Command
        Context ctxCommand = tomcat.addContext("/cmd", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctxCommand, "cmd", new CommandServlet(getApplicationSettings()));
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
