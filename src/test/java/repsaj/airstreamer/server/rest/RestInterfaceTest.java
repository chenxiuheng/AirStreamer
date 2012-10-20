/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.rest;

import java.net.URI;
import java.util.HashMap;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import repsaj.airstreamer.server.webserver.api.RestRequestException;
import repsaj.airstreamer.server.webserver.api.RestRequestHandler;

/**
 *
 * @author jasper
 */
public class RestInterfaceTest {

    private RestRequestHandler restRequestHandler;

    @Before
    public void setup() {
        try {
            String routesFile = getClass().getResource("routes.conf").toString();
            String routeUri = new URI(routesFile).getPath();

            restRequestHandler = new RestRequestHandler(routeUri);
            restRequestHandler.registerRequestHandler(new RestTestObject());

        } catch (Exception ex) {
            throw new RuntimeException("Unable to init RestRequestHandler", ex);
        }
    }

    @Test
    public void test() throws RestRequestException {
        Object ret = restRequestHandler.handleRestRequest("GET", "/test", null);
        Assert.assertEquals("test", ret);
    }

    @Test
    public void testWithId() throws RestRequestException {
        Object ret = restRequestHandler.handleRestRequest("GET", "/test/123", null);
        Assert.assertEquals("123", ret);
    }

    @Test
    public void testLatest() throws RestRequestException {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        map.put("param", new String[]{"abc"});
        Object ret = restRequestHandler.handleRestRequest("GET", "/test/latest", map);
        Assert.assertEquals("abc", ret);
    }

    @Test
    public void testMultiUrl() throws RestRequestException {
        Object ret = restRequestHandler.handleRestRequest("GET", "/series/123-abc/seasons", null);
        Assert.assertEquals(1, ret);
    }

    @Test
    public void testMultiparams() throws RestRequestException {
        Object ret = restRequestHandler.handleRestRequest("GET", "/series/abc/seasons/1/episodes", null);
        Assert.assertEquals("abc-1", ret);
    }
}
