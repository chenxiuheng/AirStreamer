package repsaj.airstreamer.server;

public class ApplicationSettings {

    private String tmpPath;
    private String resourcePath;
    private String moviePath;
    private String tvshowsPath;

    public String getTmpPath() {
        if (tmpPath == null) {
            throw new IllegalStateException("tmpPath not initialized");
        }
        return tmpPath;
    }

    public void setTmpPath(String path) {
        this.tmpPath = path;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath() {
        if (resourcePath == null) {
            throw new IllegalStateException("resourcePath not initialized");
        }
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
        if (moviePath == null) {
            throw new IllegalStateException("moviePath not initialized");
        }
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
        if (tvshowsPath == null) {
            throw new IllegalStateException("tvshowsPath not initialized");
        }
        return tvshowsPath;
    }

    /**
     * @param tvshowsPath the tvshowsPath to set
     */
    public void setTvshowsPath(String tvshowsPath) {
        this.tvshowsPath = tvshowsPath;
    }
}
