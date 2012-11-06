/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.streaming;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Subtitle;

/**
 *
 * @author jwesselink
 */
public abstract class HLSStreamConverter extends StreamPlayer {

    private static final Logger LOGGER = Logger.getLogger(HLSStreamConverter.class);

    public HLSStreamConverter(String tmpPath) {
        super(tmpPath);
    }

    @Override
    protected List<StreamInfo> doPrepare() {
        ArrayList<StreamInfo> outputStreams = new ArrayList<StreamInfo>();

        try {
            boolean subtitleMatch = false;
            boolean audioMatch = false;
            //TODO replace with setting

            ArrayList<String> subtitleLanguages = new ArrayList<String>();
            subtitleLanguages.add("def");
            subtitleLanguages.add("nl");
            subtitleLanguages.add("en");
            subtitleLanguages.add("eng");

            //First check for external subtitles

            for (String language : subtitleLanguages) {
                for (Subtitle sub : video.getSubtitles()) {
                    if (sub.getLanguage().equals(language)) {
                        subtitleMatch = true;

                        StreamInfo streamInfo = new StreamInfo();
                        streamInfo.setCodec(StreamInfo.SUBRIP);
                        streamInfo.setIndex(0);
                        streamInfo.setLanguage(sub.getLanguage());
                        streamInfo.setMediaType(StreamInfo.MediaType.Subtitle);

                        SrtToWebvvt srtToWebvvt = new SrtToWebvvt(new File(sub.getPath()));
                        srtToWebvvt.addAttachment(new HLSPlaylistGenerator());
                        srtToWebvvt.setFilesPath(tmpPath);
                        srtToWebvvt.convertStream(video, streamInfo, StreamInfo.WEBVVT);

                        StreamInfo outputStream = (StreamInfo) streamInfo.clone();
                        outputStream.setCodec(StreamInfo.WEBVVT);
                        outputStreams.add(outputStream);
                        break;
                    }
                }
                if (subtitleMatch) {
                    break;
                }
            }


            for (StreamInfo stream : mediaInfo.getStreams()) {
                switch (stream.getMediaType()) {
                    case Audio:

                        if (!audioMatch && stream.getCodec().equals(StreamInfo.AAC)) {
                            if (getAudioCodecs().contains(stream.getCodec())) {
                                audioMatch = true;
                                FfmpegWrapper ffmpegWrapper = new FfmpegWrapper();
                                ffmpegWrapper.setFilesPath(tmpPath);
                                ffmpegWrapper.addAttachment(new HLSPlaylistGenerator());
                                ffmpegWrapper.convertStream(video, stream, null);

                                StreamInfo outputStream = (StreamInfo) stream.clone();
                                outputStreams.add(outputStream);
                            }

                        } else if (!audioMatch && stream.getCodec().equals(StreamInfo.AC3)) {

                            audioMatch = true;
                            if (getAudioCodecs().contains(stream.getCodec())) {
                                //Extract ac3 stream:
                                FfmpegWrapper ffmpegWrapper1 = new FfmpegWrapper();
                                ffmpegWrapper1.setFilesPath(tmpPath);
                                ffmpegWrapper1.addAttachment(new HLSPlaylistGenerator());
                                ffmpegWrapper1.convertStream(video, stream, null);

                                StreamInfo outputStream1 = (StreamInfo) stream.clone();
                                outputStreams.add(outputStream1);

                            } else {
                                //Convert ac3 to aac stream:
                                FfmpegWrapper ffmpegWrapper2 = new FfmpegWrapper();
                                ffmpegWrapper2.setFilesPath(tmpPath);
                                ffmpegWrapper2.addAttachment(new HLSPlaylistGenerator());
                                ffmpegWrapper2.convertStream(video, stream, StreamInfo.AAC);

                                StreamInfo outputStream2 = (StreamInfo) stream.clone();
                                outputStream2.setCodec(StreamInfo.AAC);
                                outputStreams.add(outputStream2);
                            }

                        }
                        break;

                    case Video:
                        if (!stream.getCodec().equals(StreamInfo.H264)) {
                            throw new UnsupportedOperationException("Codec not supported:" + stream.getCodec());
                        }

                        FfmpegWrapper ffmpegWrapper = new FfmpegWrapper();
                        ffmpegWrapper.setFilesPath(tmpPath);
                        ffmpegWrapper.addAttachment(new HLSPlaylistGenerator());
                        ffmpegWrapper.convertStream(video, stream, null);

                        StreamInfo outputStream = (StreamInfo) stream.clone();
                        outputStreams.add(outputStream);

                        break;

                    case Subtitle:

                        if (!subtitleMatch && stream.getCodec().equals(StreamInfo.SUBRIP) && subtitleLanguages.contains(stream.getLanguage())) {
                            FfmpegWrapper ffmpegWrappersub = new FfmpegWrapper();
                            ffmpegWrappersub.setFilesPath(tmpPath);

                            File output = new File(ffmpegWrappersub.getOutputFile(false, "srt"));
                            SrtToWebvvt srtToWebvvt = new SrtToWebvvt(output);
                            srtToWebvvt.setUpAsAttachment(video, stream, StreamInfo.WEBVVT);

                            ffmpegWrappersub.addAttachment(srtToWebvvt);
                            ffmpegWrappersub.convertStream(video, stream, null);
                            subtitleMatch = true;

                            StreamInfo outputStreamSub = (StreamInfo) stream.clone();
                            outputStreamSub.setCodec(StreamInfo.WEBVVT);
                            outputStreams.add(outputStreamSub);
                        }

                        break;
                }
            }

        } catch (Exception ex) {
            LOGGER.error("error in prepare player", ex);
        }
        return outputStreams;
    }

    public abstract List<String> getVideoCodecs();

    public abstract List<String> getAudioCodecs();

    public abstract List<String> getSubtitleCodecs();
}
