/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import java.util.regex.Pattern;

/**
 *
 * @author jasper
 */
public class Route {

    private String httpMethod;
    private String path;
    private Pattern pathPattern;
    private String method;

    public Route() {
    }

    public Route(String httpMethod, String path, String method) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.method = method;

        String tmpPath = path.replaceAll("\\{[a-zA-Z]+\\}", "([^/]+)");
        tmpPath = "^" + tmpPath + "$";
        this.pathPattern = Pattern.compile(tmpPath);
    }

    /**
     * @return the httpMethod
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * @param httpMethod the httpMethod to set
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the pathPattern
     */
    public Pattern getPathPattern() {
        return pathPattern;
    }

    /**
     * @param pathPattern the pathPattern to set
     */
    public void setPathPattern(Pattern pathPattern) {
        this.pathPattern = pathPattern;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }
}
