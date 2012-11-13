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
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public class SrtToWebvvt extends StreamConverter implements JobAttachment {

    private static final Logger LOGGER = Logger.getLogger(SrtToWebvvt.class);
    private File inputSrt;
    private Writer outputFileWriter;

    public SrtToWebvvt() {
    }

    public SrtToWebvvt(File inputSrt) {
        this.inputSrt = inputSrt;
        this.segmentTime = 60;
    }

    @Override
    protected void doConvert() {
        try {
            InputStream inputStream = new FileInputStream(inputSrt);

            for (JobAttachment attachment : jobAttachments) {
                //set monitor to false because we can tell the attachment to update when a new file is created
                attachment.setMonitorPath(false);
                attachment.start(outputPath, segmentTime);
            }

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

        for (JobAttachment attachment : jobAttachments) {
            attachment.finish();
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

        for (JobAttachment attachment : jobAttachments) {
            attachment.update();
        }

        String filenamePath = getOutputPath() + String.format("subtitle%04d.vvt", segmentCounter);
        outputFileWriter = new BufferedWriter(new FileWriter(filenamePath));

        outputFileWriter.write("WEBVTT\n");
        outputFileWriter.write("X-TIMESTAMP-MAP=MPEGTS:0, LOCAL:00:00:00.000\n");

    }

    // ==========================
    // JobAttachment methods
    // ==========================
    @Override
    public void start(final String path, int segmentTime) {

        Thread tmp = new Thread() {
            @Override
            public void run() {
                //Wait for subtitle file to be generated
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }

                Collection<File> files = FileUtils.listFiles(new File(path), new String[]{"srt"}, false);
                if (files.isEmpty()) {
                    throw new RuntimeException("No subtitle found in:" + path);
                } else {
                    for (File file : files) {
                        LOGGER.info("Setting inputSrt to:" + file.getName());
                        inputSrt = file;
                        break;
                    }
                }
            }
        };
        tmp.start();
        
    }

    @Override
    public void finish() {
        doConvert();
    }

    @Override
    public void update() {
    }

    @Override
    public void setMonitorPath(boolean doMonitor) {
        if (doMonitor) {
            //setup a task to monitor file
        }
    }

    public void setUpAsAttachment(Video video, StreamInfo streamInfo, String toCodec) {
        //TODO find a better way, this is now a copy of StreamConverter#convertStream

        if (getFilesPath() == null) {
            throw new IllegalStateException("filesPath not set!");
        }
        this.video = video;
        this.streamInfo = streamInfo;
        this.toCodec = toCodec;
        determineOutputPath();
        ensureOutputPathExists();
    }
}
