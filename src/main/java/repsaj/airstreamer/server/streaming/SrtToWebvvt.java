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
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public class SrtToWebvvt {

    private static final Logger LOGGER = Logger.getLogger(SrtToWebvvt.class);
    private File inputSrt;
    private int segmentTime;
    private String outputPath;
    private Writer outputFileWriter;

    public SrtToWebvvt(File inputSrt, int segmentTime, String outputPath) {
        this.inputSrt = inputSrt;
        this.segmentTime = segmentTime;
        this.outputPath = outputPath;
    }

    public void parse() {

        try {
            InputStream inputStream = new FileInputStream(inputSrt);
            parse(inputStream);
        } catch (IOException ex) {
            LOGGER.error("error parsing file", ex);
        }
    }

    private void parse(InputStream is) throws IOException {

        int segmentCounter = 0;
        closeAndCreateNewFile(segmentCounter);

        LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
        String numberString = null;
        while ((numberString = r.readLine()) != null) {

            //Fix for subtitles with then one empty lines
            while (numberString.trim().isEmpty()) {
                numberString = r.readLine();
            }

            String timeString = r.readLine();
            String lineString = "";
            String s;
            while (!((s = r.readLine()) == null || s.trim().isEmpty())) {
                lineString += s + "\n";
            }

            long startTime = parseSrtTime(timeString.split("-->")[0]);
            long endTime = parseSrtTime(timeString.split("-->")[1]);

            if ((int) (startTime / (segmentTime * 1000)) != segmentCounter) {
                //set new segmentcounter
                segmentCounter = newSegmentCounter(segmentCounter, startTime);
            }
            if ((int) (endTime / (segmentTime * 1000)) != segmentCounter) {
                //write linestring
                writeWebvvt(timeString, lineString);
                //create new segment
                segmentCounter = newSegmentCounter(segmentCounter, endTime);
            }

            writeWebvvt(timeString, lineString);
        }

        if (outputFileWriter != null) {
            try {
                outputFileWriter.close();
            } catch (IOException ex) {
                LOGGER.error("error closing file", ex);
            }
        }

    }

    private int newSegmentCounter(int oldSegment, long time) throws IOException {

        int targetSegment = (int) (time / (segmentTime * 1000));

        for (int counter = oldSegment + 1; counter <= targetSegment; counter++) {
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

    private String toWebvvtTime(String strTimeString) {

        return strTimeString.trim().replace(",", ".");
    }

    private void writeWebvvt(String dateString, String linestring) throws IOException {

        String outputString = "\n";
        outputString += toWebvvtTime(dateString) + "\n";
        outputString += linestring;

        outputFileWriter.write(outputString);

    }

    private void closeAndCreateNewFile(int segmentCounter) throws IOException {

        if (outputFileWriter != null) {
            try {
                outputFileWriter.close();
            } catch (IOException ex) {
                LOGGER.error("error closing file", ex);
            }
        }

        String filename = String.format("subtitle%04d.vvt", segmentCounter);

        String filenamePath = outputPath + filename;
        outputFileWriter = new BufferedWriter(new FileWriter(filenamePath));

        outputFileWriter.write("WEBVTT\n");
        outputFileWriter.write("X-TIMESTAMP-MAP=MPEGTS:0, LOCAL:00:00:00.000\n");

    }
}
