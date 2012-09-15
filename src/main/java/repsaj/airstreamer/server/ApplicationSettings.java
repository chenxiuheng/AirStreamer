package repsaj.airstreamer.server;

/**
 * Created with IntelliJ IDEA.
 * User: jasper
 * Date: 9/6/12
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationSettings {

    private String path = null;

    public ApplicationSettings() {
    }

    public void initialize(String path) {
        this.path = path;
    }

    /**
     * @return the path
     */
    public String getPath() {
        if (path == null) {
            throw new IllegalStateException("path not initialized");
        }
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
}
