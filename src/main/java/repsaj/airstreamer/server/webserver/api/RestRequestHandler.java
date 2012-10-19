/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.webserver.api;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public class RestRequestHandler {

    private static final Logger LOGGER = Logger.getLogger(RestRequestHandler.class);
    private static final Pattern ROUTE_PATTERN = Pattern.compile("([A-Z*]+)[ \\t]+([a-zA-Z/{}]+)[ \\t]+([a-zA-Z.()]+)");
    private ArrayList<Route> routeList = new ArrayList<Route>();
    private HashMap<String, Object> requestHandlers = new HashMap<String, Object>();

    public RestRequestHandler(String routeConfigPath) {

        try {

            String routes = FileUtils.readFileToString(new File(routeConfigPath));
            int index = 0;
            int eol = 0;
            while (eol != -1) {
                eol = routes.indexOf("\n", index);
                if (eol >= 0) {
                    String routeStr = routes.substring(index, eol);
                    Matcher match = ROUTE_PATTERN.matcher(routeStr);
                    if (match.find()) {
                        Route route = new Route(match.group(1), match.group(2), match.group(3));
                        routeList.add(route);
                    }
                }
                index = eol + 1;
            }
        } catch (Exception ex) {
            LOGGER.error("Error loading routes file", ex);
        }
    }

    public void registerRequestHandler(Object obj) {
        requestHandlers.put(obj.getClass().getSimpleName(), obj);
    }

    public Object handleRestRequest(String method, String urlPath, Map<String, String[]> parameters) {
        for (Route route : routeList) {
            if (method.equals(route.getHttpMethod()) || "*".equals(route.getHttpMethod())) {
                Matcher matcher = route.getPathPattern().matcher(urlPath);
                if (matcher.find()) {
                    LOGGER.info(method + " found match with: " + route.getPath());

                    ArrayList<String> urlArgs = new ArrayList<String>();
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        urlArgs.add(matcher.group(i + 1));
                    }
                    return handleRoute(route, parameters, urlArgs);
                }
            }
        }
        return null;
    }

    private Object handleRoute(Route route, Map<String, String[]> parameters, ArrayList<String> urlArgs) {
        String[] tmp = route.getMethod().split("\\.");
        String clazz = tmp[0];
        String clazzMethod = tmp[1];

        String methodName = clazzMethod.substring(0, clazzMethod.indexOf("("));

        String argString = clazzMethod.substring(clazzMethod.indexOf("(") + 1, clazzMethod.indexOf(")"));

        String[] argArray;
        if (!argString.trim().isEmpty()) {
            argArray = argString.split(",");
        } else {
            argArray = new String[]{};
        }

        Object[] args = new Object[argArray.length];
        Class[] argsClazz = new Class[argArray.length];
        for (int i = 0; i < argArray.length; i++) {
            String argName = argArray[i].trim();

            String argValue = "";
            if (route.getPath().contains("{" + argName + "}")) {
                //try to find the arg in the url part
                String subpath = route.getPath().substring(0, route.getPath().indexOf("{" + argName + "}") + 1);
                int paramindex = StringUtils.countMatches(subpath, "{") - 1;
                argValue = urlArgs.get(paramindex);
            } else {
                //try to find the arg as parameter in the url
                if (parameters.containsKey(argName)) {
                    argValue = parameters.get(argName)[0];
                }
            }

            argsClazz[i] = String.class;
            args[i] = argValue;
        }

        Object obj = requestHandlers.get(clazz);
        try {
            Method method = obj.getClass().getMethod(methodName, argsClazz);
            Object returnObj = method.invoke(obj, args);
            LOGGER.info("return: " + returnObj);
            return returnObj;
        } catch (Exception ex) {
            LOGGER.error("Error executing method", ex);
            return null;
        }
    }
}
