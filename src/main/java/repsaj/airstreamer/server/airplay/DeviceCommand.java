/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.airplay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public abstract class DeviceCommand {

    private static Logger logger = Logger.getLogger(DeviceCommand.class.getName());
    private Map<String, String> parameterMap = new HashMap<String, String>();

    public enum Type {

        GET, POST;
    }

    protected void addParameter(String name, Object value) {
        parameterMap.put(name, value.toString());
    }

    protected String constructCommand(String commandName, String content, Type type) {
        String parameterValue = parameterMap.keySet().isEmpty() ? "" : "?";
        for (String key : parameterMap.keySet()) {
            try {
                parameterValue += key + "=" + URLEncoder.encode(parameterMap.get(key), "utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        String headerPart = String.format("%s /%s%s HTTP/1.1\n"
                + "Content-Length: %d\n"
                + "User-Agent: MediaControl/1.0\n", type.name(), commandName, parameterValue, content == null ? 0 : content.length());
        if (content == null || content.length() == 0) {
            return headerPart;
        } else {
            return headerPart + "\n" + content;
        }
    }

    public abstract String getCommandString();
}
