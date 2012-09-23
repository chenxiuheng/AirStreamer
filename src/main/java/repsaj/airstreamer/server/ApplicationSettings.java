package repsaj.airstreamer.server;

public class ApplicationSettings {

    private String tmpPath;
    private String resourcePath;

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
}
