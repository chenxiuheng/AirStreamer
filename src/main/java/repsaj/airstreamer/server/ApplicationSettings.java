package repsaj.airstreamer.server;


public class ApplicationSettings {

    private static String path = null;

    public static void initialize(String path) {
        ApplicationSettings.path = path;
    }

    /**
     * @return the path
     */
    public static String getPath() {
        if (path == null) {
            throw new IllegalStateException("path not initialized");
        }
        return path;
    }

    /**
     * @param path the path to set
     */
    public static void setPath(String path) {
        ApplicationSettings.path = path;
    }
}
