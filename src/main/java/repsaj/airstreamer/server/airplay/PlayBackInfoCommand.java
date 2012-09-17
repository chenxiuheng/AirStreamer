/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package repsaj.airstreamer.server.airplay;

/**
 *
 * @author jasper
 */
public class PlayBackInfoCommand extends DeviceCommand {

    @Override
    public String getCommandString() {
        return constructCommand("playback-info", null, Type.GET);
    }
}