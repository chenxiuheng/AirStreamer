/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import repsaj.airstreamer.server.model.TvShowSerie;
import repsaj.airstreamer.server.model.TvShowEpisode;

/**
 *
 * @author jasper
 */
public class TvShowDirectoryIndexer {

    //S01E01
    private static final Pattern SEASON_EPISODE_1 = Pattern.compile(".*[sS]([\\d]+)[eE]([\\d]+).*");
    //1x01
    private static final Pattern SEASON_EPISODE_2 = Pattern.compile(".*([\\d]+)[xX]([\\d]+).*");
    private static final String SUBTITLE = "srt";

    public List<TvShowSerie> indexTvShows(String path) {
        ArrayList<TvShowSerie> tvShows = new ArrayList<TvShowSerie>();

        File tvshow_path = new File(path);

        File[] files = tvshow_path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {

                System.out.println("TvShow:" + file.getName());

                TvShowSerie show = new TvShowSerie();
                show.setName(file.getName());
                show.setPath(file.getPath());
                tvShows.add(show);

            }
        }

        return tvShows;
    }

    public List<TvShowEpisode> indexTvShow(TvShowSerie serie) {
        ArrayList<TvShowEpisode> tvshows = new ArrayList<TvShowEpisode>();
        File tvserie_path = new File(serie.getPath());
        tvshows.addAll(indexSeasons(tvserie_path, serie));
        return tvshows;
    }

    private List<TvShowEpisode> indexSeasons(File path, TvShowSerie serie) {
        ArrayList<TvShowEpisode> tvshows = new ArrayList<TvShowEpisode>();

        File[] files = path.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Season:" + file.getName());
                String seasonstr = file.getName().replaceAll("\\D+", "");
                try {
                    int season = Integer.valueOf(seasonstr);
                    System.out.println(">>Found season [" + season + "]");
                    tvshows.addAll(indexEpisodes(file, season, serie));
                } catch (NumberFormatException ex) {
                }
            }
        }

        return tvshows;
    }

    private List<TvShowEpisode> indexEpisodes(File path, int season, TvShowSerie serie) {
        ArrayList<TvShowEpisode> tvshows = new ArrayList<TvShowEpisode>();
        Matcher matcher;

        File[] files = path.listFiles();

        for (File file : files) {

            if (file.isFile()) {
                System.out.println("file:" + file.getName());

                String extenstion = FilenameUtils.getExtension(file.getName());
                if (SUBTITLE.equals(extenstion)) {
                    //TODO handle subtitles
                    continue;
                }

                matcher = SEASON_EPISODE_1.matcher(file.getName());
                if (matcher.matches()) {
                    System.out.println(">>Found Season:[" + matcher.group(1) + "] Episode:[" + matcher.group(2) + "]");
                    int iSeason = Integer.valueOf(matcher.group(1));
                    int iEpisode = Integer.valueOf(matcher.group(2));
                    TvShowEpisode tvShowEpisode = createTvShowEpisode(file, iSeason, iEpisode, serie.getId());
                    tvshows.add(tvShowEpisode);
                    continue;
                }

                matcher = SEASON_EPISODE_2.matcher(file.getName());
                if (matcher.matches()) {
                    System.out.println(">>Found Season:[" + matcher.group(1) + "] Episode:[" + matcher.group(2) + "]");
                    int iSeason = Integer.valueOf(matcher.group(1));
                    int iEpisode = Integer.valueOf(matcher.group(2));
                    TvShowEpisode tvShowEpisode = createTvShowEpisode(file, iSeason, iEpisode, serie.getId());
                    tvshows.add(tvShowEpisode);
                    continue;
                }
            }
        }

        return tvshows;
    }

    private TvShowEpisode createTvShowEpisode(File file, int season, int episode, String serieId) {
        TvShowEpisode tvShowEpisode = new TvShowEpisode(serieId, season, episode);
        tvShowEpisode.setSerieId(serieId);
        tvShowEpisode.setName(FilenameUtils.removeExtension(file.getName()));
        tvShowEpisode.setPath(file.getPath());

        return tvShowEpisode;
    }
}
