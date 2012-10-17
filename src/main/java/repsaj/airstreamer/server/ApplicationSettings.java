package repsaj.airstreamer.server;

import java.io.FileInputStream;
import java.util.Properties;

public class ApplicationSettings {

    private String tmpPath;
    private String resourcePath;
    private String moviePath;
    private String tvshowsPath;
    private String ip;

    public void load() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("config.properties"));
            tmpPath = prop.getProperty("tmpPath");
            resourcePath = prop.getProperty("resourcePath");
            moviePath = prop.getProperty("moviePath");
            tvshowsPath = prop.getProperty("tvshowsPath");
            ip = prop.getProperty("ip");
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load settings", ex);
        }
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String path) {
        this.tmpPath = path;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @param resourcePath the resourcePath to set
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * @return the moviePath
     */
    public String getMoviePath() {
        return moviePath;
    }

    /**
     * @param moviePath the moviePath to set
     */
    public void setMoviePath(String moviePath) {
        this.moviePath = moviePath;
    }

    /**
     * @return the tvshowsPath
     */
    public String getTvshowsPath() {
        return tvshowsPath;
    }

    /**
     * @param tvshowsPath the tvshowsPath to set
     */
    public void setTvshowsPath(String tvshowsPath) {
        this.tvshowsPath = tvshowsPath;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
}
