/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.TvShowEpisode;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FilenameUtils;
import org.xml.sax.InputSource;
import repsaj.airstreamer.server.model.Subtitle;
import repsaj.airstreamer.server.model.TvShowSerie;

/**
 *
 * @author jasper
 */
public class BierdopjeApi {

    private static final Logger LOGGER = Logger.getLogger(BierdopjeApi.class);
    private static final String API_KEY = "0EC65CAD4A5A1654";
    private static final String USERAGENT = "AirStreamer/1.0";

    public void findSubtitles(TvShowSerie serie, TvShowEpisode episode) {

        String[] languages = new String[]{"nl", "en"};

        for (String language : languages) {
            String downloadUrl = findSubtitleUrlForLanguage(serie, episode, language);
            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                downloadAndStoreSubtitle(downloadUrl, language, episode);
                break;
            }
        }
    }

    private void downloadAndStoreSubtitle(String url, String language, TvShowEpisode episode) {
        String targetFilename = FilenameUtils.removeExtension(episode.getPath()) + ".srt";

        Subtitle subtitle = new Subtitle();
        subtitle.setExternal(true);
        subtitle.setLanguage(language);
        subtitle.setPath(targetFilename);

        ResourceManager.getInstance().download(subtitle, url, episode, false);
    }

    private String findSubtitleUrlForLanguage(TvShowSerie serie, TvShowEpisode episode, String language) {

        String filename = FilenameUtils.getBaseName(episode.getPath());

        String url = "http://api.bierdopje.com/" + API_KEY + "/GetAllSubsFor/"
                + serie.getShowId() + "/" + episode.getSeason() + "/" + episode.getEpisode() + "/"
                + language + "/true";

        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter("http.useragent", USERAGENT);
        int httpResponse = 0;
        String response = null;
        try {
            httpResponse = client.executeMethod(getMethod);
            if (httpResponse == 200) {
                response = getMethod.getResponseBodyAsString();
                LOGGER.info("response:" + response);

                String downloadUrl = parseResponse(response, filename);
                LOGGER.info("downloadUrl:" + downloadUrl);
                return downloadUrl;

            } else {
                LOGGER.error("Http call to [" + url + "] failed, response: " + httpResponse);
            }
        } catch (IOException ex) {
            LOGGER.error("Http call to [" + url + "] failed", ex);
        }
        return null;
    }

    private String parseResponse(String response, String filename) {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response));
            Document doc = builder.parse(is);
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile("//result[filename='" + filename + "']/downloadlink");

            String result = (String) expr.evaluate(doc, XPathConstants.STRING);
            LOGGER.error(">> result:" + result);
            return result;


        } catch (Exception ex) {
            LOGGER.error("Error parsing doc", ex);
        }
        return null;
    }
}
