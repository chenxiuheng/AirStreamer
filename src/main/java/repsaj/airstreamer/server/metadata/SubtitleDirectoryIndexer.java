/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Subtitle;

/**
 *
 * @author jasper
 */
public class SubtitleDirectoryIndexer {

    private static final Logger LOGGER = Logger.getLogger(SubtitleDirectoryIndexer.class);
    private static final String[] SUPPORTED_SUB_EXT = {"srt"};
    private static final String[] SUPPORTED_LANGUAGES = {"en", "nl"};
    private static final String DEFAULT_LANGUAGE = "def";

    public List<Subtitle> indexSubtitles(String path) {
        List<Subtitle> subtitles = new ArrayList<Subtitle>();

        File search_path = new File(path);
        Collection<File> subtitleFiles = FileUtils.listFiles(search_path, SUPPORTED_SUB_EXT, true);

        for (File subtitleFile : subtitleFiles) {
            LOGGER.info("found: " + subtitleFile.getPath());
            Subtitle sub = new Subtitle();
            sub.setPath(subtitleFile.getPath());
            sub.setExternal(true);
            extractLanguage(subtitleFile.getName(), sub);
            subtitles.add(sub);
        }

        return subtitles;
    }

    private void extractLanguage(String filename, Subtitle sub) {
        String name = FilenameUtils.removeExtension(filename);

        int index = name.lastIndexOf(".");
        if (index > 0) {
            String languageCode = name.substring(index + 1, name.length());
            for (String code : SUPPORTED_LANGUAGES) {
                if (code.equalsIgnoreCase(languageCode)) {
                    sub.setLanguage(code);
                    break;
                }
            }
        }

        if (sub.getLanguage() == null) {
            sub.setLanguage(DEFAULT_LANGUAGE);
        }

    }
}
