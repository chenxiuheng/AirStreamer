/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

/**
 *
 * @author jasper
 */
public interface JobAttachment {

    public void start(String path, int segmentTime);

    public void finish();

    public void update();

    public void setMonitorPath(boolean doMonitor);

    public void addAttachment(JobAttachment attachment);
}
