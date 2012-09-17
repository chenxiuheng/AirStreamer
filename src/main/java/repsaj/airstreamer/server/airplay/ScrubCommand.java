/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.airplay;

/**
 *
 * @author jasper
 */
public class ScrubCommand extends DeviceCommand {

    private Type type = Type.GET;
    private float position = 0;

    public void setPosition(float position) {
        this.type = Type.POST;
        this.position = position;
    }

    @Override
    public String getCommandString() {

        if (type.equals(Type.GET)) {
            return constructCommand("scrub", null, type);
        } else {
            addParameter("position", position);
            return constructCommand("scrub?position=", null, type);
        }
    }
}
