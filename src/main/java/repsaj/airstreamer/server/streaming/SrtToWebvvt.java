/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Writer;

/**
 *
 * @author jasper
 */
public class SrtToWebvvt {

    private File inputSrt;
    private File outputWebvvt;
    private int segmentTime;
    private String outputPath;
    private Writer outputFileWriter;

    public SrtToWebvvt(File inputSrt, int segmentTime, String outputPath) {
        this.inputSrt = inputSrt;
        this.segmentTime = segmentTime;
        this.outputPath = outputPath;
    }

    public void start() {

        try {
            InputStream inputStream = new FileInputStream(inputSrt);
            parse(inputStream);
        } catch (IOException ex) {
        }
    }

    public void stop() {
    }

    private void parse(InputStream is) throws IOException {

        int segmentCounter = 0;
        closeAndCreateNewFile(segmentCounter);

        LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
        String numberString = null;
        while ((numberString = r.readLine()) != null) {
            String timeString = r.readLine();
            String lineString = "";
            String s;
            while (!((s = r.readLine()) == null || s.trim().equals(""))) {
                lineString += s + "\n";
            }

            long startTime = parseSrtTime(timeString.split("-->")[0]);
            long endTime = parseSrtTime(timeString.split("-->")[1]);

            if ((int) (startTime / segmentTime * 1000) != segmentCounter) {
                //set new segmentcounter
                segmentCounter = newSegmentCounter(segmentCounter, startTime);
            }
            if ((int) (endTime / segmentTime * 1000) != segmentCounter) {
                //write linestring
                writeWebvvt(startTime, endTime, lineString, outputFileWriter);
                //create new segment
                segmentCounter = newSegmentCounter(segmentCounter, startTime);
            }

            writeWebvvt(startTime, endTime, lineString, outputFileWriter);
        }

    }

    private int newSegmentCounter(int oldSegment, long time) throws IOException {

        int targetSegment = (int) (time / segmentTime * 1000);

        for (int counter = oldSegment; counter <= targetSegment; counter++) {
            closeAndCreateNewFile(counter);
        }

        return targetSegment;
    }

    private long parseSrtTime(String in) {
        long hours = Long.parseLong(in.split(":")[0].trim());
        long minutes = Long.parseLong(in.split(":")[1].trim());
        long seconds = Long.parseLong(in.split(":")[2].split(",")[0].trim());
        long millies = Long.parseLong(in.split(":")[2].split(",")[1].trim());

        return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + millies;

    }

    private String toWebvvtTime(long time) {
        return "";
    }

    private void writeWebvvt(long startTime, long endTime, String linestring, Writer out) throws IOException {
        String outputString = "\n";
        outputString += toWebvvtTime(startTime) + " --> " + toWebvvtTime(endTime) + "\n";
        outputString += linestring;

        out.write(outputString);
    }

    private void closeAndCreateNewFile(int segmentCounter) throws IOException {

        if (outputFileWriter != null) {
            try {
                outputFileWriter.close();
            } catch (IOException ex) {
            }
        }

        String filename = outputPath + "subtitle" + segmentCounter + ".vvt";
        outputFileWriter = new BufferedWriter(new FileWriter(filename));

        outputFileWriter.write("WEBVTT\n");
        outputFileWriter.write("X-TIMESTAMP-MAP=MPEGTS:0, LOCAL:00:00:00.000\n");

    }
}
