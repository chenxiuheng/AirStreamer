/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.airplay;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jasper
 */
public class DeviceResponse {

    private int responseCode;
    private String responseMessage;
    private Map<String, String> headerMap = new HashMap<String, String>();
    private String content;
    private Map<String, String> contentParameterMap = new HashMap<String, String>();

    public DeviceResponse(String headers, String content) {
        String headerSplit[] = headers.split("\n");

        String rawResponseValue = headerSplit[0];
        String responseSplit[] = rawResponseValue.split(" ");
        responseCode = Integer.parseInt(responseSplit[1]);
        responseMessage = responseSplit[2];

        for (int i = 1; i < headerSplit.length; i++) {
            String headerValueSplit[] = headerSplit[i].split(":");
            headerMap.put(headerValueSplit[0], headerValueSplit[1].trim());
        }

        if (content != null) {
            this.content = content;

            if ("text/parameters".equalsIgnoreCase(headerMap.get("Content-Type"))) {
                for (String paramLine : content.split("\n")) {
                    String paramSplit[] = paramLine.split(":");
                    contentParameterMap.put(paramSplit[0], paramSplit[1].trim());
                }
            }
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public String getContent() {
        return content;
    }

    public boolean hasContentParameters() {
        return !contentParameterMap.isEmpty();
    }

    public Map<String, String> getContentParameterMap() {
        return contentParameterMap;
    }
}
