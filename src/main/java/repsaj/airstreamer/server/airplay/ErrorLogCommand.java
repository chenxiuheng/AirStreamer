/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repsaj.airstreamer.server.airplay;

/**
 *
 * @author jasper
 */
public class ErrorLogCommand  extends DeviceCommand {

    @Override
    public String getCommandString() {
        return constructCommand("getProperty?playbackErrorLog", null);
    }
}