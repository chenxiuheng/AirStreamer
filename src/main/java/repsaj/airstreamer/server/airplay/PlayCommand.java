/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.airplay;

/**
 *
 * @author jasper
 */
public class PlayCommand extends DeviceCommand {

    private String contentURL;
    private double startPosition;

    public PlayCommand(String contentURL, double startPosition) {
        this.contentURL = contentURL;
        this.startPosition = startPosition;
    }

    @Override
    public String getCommandString() {
        return constructCommand("play",
                String.format("Content-Location: %s\n"
                + "Start-Position: %f\n", contentURL, startPosition));
    }
}
