/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author jasper
 */
public class HLSMasterPlaylistGenerator {

    private static final Logger LOGGER = Logger.getLogger(HLSMasterPlaylistGenerator.class);
    private static final String NEW_LINE = "\n";
    private File playlistFile;

    public void start(List<StreamInfo> streams, String path) {
        playlistFile = new File(path + "/index.m3u8");

        StringBuilder builder = new StringBuilder();
        builder.append("#EXTM3U").append(NEW_LINE).append(NEW_LINE);
        boolean hasSubs = false;

        for (StreamInfo stream : streams) {
            if (stream.getMediaType().equals(StreamInfo.MediaType.Audio)) {

                String defaultText;
                if (stream.getCodec().equals("libfaac")) {
                    defaultText = "YES";
                } else {
                    defaultText = "NO";
                }

                builder.append("#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"group1\",URI=\"Audio_");
                builder.append(stream.getCodec()).append("/index.m3u8\", AUTOSELECT=YES,LANGUAGE=\"en\",NAME=\"English " + stream.getCodec() + "\",DEFAULT=").append(defaultText);
                builder.append(NEW_LINE);
            }

            if (stream.getMediaType().equals(StreamInfo.MediaType.Subtitle)) {
                hasSubs = true;
                builder.append("#EXT-X-MEDIA:TYPE=SUBTITLES,GROUP-ID=\"subs\",NAME=\"English\",DEFAULT=YES,AUTOSELECT=YES,FORCED=NO,LANGUAGE=\"" + stream.getLanguage() + "\",URI=\"Subtitle_vvt_" + stream.getLanguage() + "/index.m3u8\"");
                builder.append(NEW_LINE);
            }
        }
        builder.append(NEW_LINE);


        for (StreamInfo stream : streams) {

            if (stream.getMediaType().equals(StreamInfo.MediaType.Video)) {
                builder.append("#EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=7000000, CODECS=\"avc1.4d001f\", AUDIO=\"group1\"");
                if (hasSubs) {
                    builder.append(", SUBTITLES=\"subs\"");
                }
                builder.append(NEW_LINE);
                builder.append(getPath(stream));
                builder.append(NEW_LINE);
            }
            if (stream.getMediaType().equals(StreamInfo.MediaType.Audio)) {
                if ("libfaac".equals(stream.getCodec())) {
                    builder.append("#EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=384000, CODECS=\"mp4a.40.5\", AUDIO=\"group1\"");

                }
                if ("ac3".equals(stream.getCodec())) {
                    builder.append("#EXT-X-STREAM-INF:PROGRAM-ID=1, BANDWIDTH=384000, CODECS=\"ac-3\", AUDIO=\"group1\"");
                }
                if (hasSubs) {
                    builder.append(", SUBTITLES=\"subs\"");
                }
                builder.append(NEW_LINE);
                builder.append(getPath(stream));
                builder.append(NEW_LINE);
            }

        }

        try {
            FileUtils.writeStringToFile(playlistFile, builder.toString());
        } catch (IOException ex) {
            LOGGER.error("error writing playlistfile", ex);
        }
    }

    private String getPath(StreamInfo stream) {

        
        String path = stream.getMediaType().name();
        path += "_" + stream.getCodec();
        if (stream.getLanguage() != null) {
            path += "_" + stream.getLanguage();
        }
        path += "/index.m3u8";

        return path;
    }
}
