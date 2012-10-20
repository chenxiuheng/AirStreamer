/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import java.io.IOException;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import repsaj.airstreamer.server.ApplicationSettings;
import repsaj.airstreamer.server.db.Database;

/**
 *
 * @author jasper
 */
public class RestServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RestServlet.class);
    private ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
    private RestRequestHandler restRequestHandler;
    private Database db;
    private ApplicationSettings applicationSettings;

    public RestServlet(Database db, ApplicationSettings applicationSettings) {
        this.db = db;
        this.applicationSettings = applicationSettings;

        try {
            String routesFile = getClass().getResource("routes.conf").toString();
            String routeUri = new URI(routesFile).getPath();

            restRequestHandler = new RestRequestHandler(routeUri);
            restRequestHandler.registerRequestHandler(new Movies(db));
            restRequestHandler.registerRequestHandler(new Series(db));
            restRequestHandler.registerRequestHandler(new Application(db, applicationSettings));

        } catch (Exception ex) {
            throw new RuntimeException("Unable to init RestRequestHandler", ex);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("GET Rest request for: " + request.getPathInfo());
        try {
            Object ret = restRequestHandler.handleRestRequest("GET", request.getPathInfo(), request.getParameterMap());
            writeResponse(ret, response);
        } catch (RestRequestException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("POST Rest request for: " + request.getPathInfo());
        try {
            Object ret = restRequestHandler.handleRestRequest("POST", request.getPathInfo(), request.getParameterMap());
            writeResponse(ret, response);
        } catch (RestRequestException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("PUT Rest request for: " + request.getPathInfo());
        try {
            Object ret = restRequestHandler.handleRestRequest("PUT", request.getPathInfo(), request.getParameterMap());
            writeResponse(ret, response);
        } catch (RestRequestException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("DELETE Rest request for: " + request.getPathInfo());
        try {
            Object ret = restRequestHandler.handleRestRequest("DELETE", request.getPathInfo(), request.getParameterMap());
            writeResponse(ret, response);
        } catch (RestRequestException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private void writeResponse(Object obj, HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            String message = writer.writeValueAsString(obj);
            response.getWriter().print(message);
        } catch (IOException ex) {
            LOGGER.error("Error writing response", ex);
        }
    }
}
