package com.sednar.digital.media.service.content.video;

import com.sednar.digital.media.common.util.ProcessUtil;
import com.sednar.digital.media.service.config.properties.VideoProcessingProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.MessageFormat;

@Service
public class FFmpegVideoService {

    private static final String FFPROBE_COMMAND = "\"{0}\\{1}\" -loglevel error -of csv=p=0 -show_entries format=duration \"{2}\"";

    private static final String FFMPEG_GIF = "\"{0}\\{1}\" -ss {2,number,#} -t {3} -i \"{4}\" -vf \"fps=10,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse\" -loop 0 \"{5}\"";

    private final VideoProcessingProps properties;

    @Autowired
    FFmpegVideoService(VideoProcessingProps properties) {
        this.properties = properties;
    }

    File generateThumbnail(File file, double videoLength) {
        int grabAtPercent = properties.getGrabAtPercent();
        int grabPosition = ((int)videoLength)/(100/grabAtPercent);
        File thumbnail = new File(file.getParent(), file.getName() + properties.getSuffix());
        String command = MessageFormat.format(FFMPEG_GIF,
                properties.getProcessorPath().getAbsolutePath(),
                properties.getGenerator(),
                grabPosition,
                properties.getGifDuration(),
                file.getAbsolutePath(),
                thumbnail.getAbsolutePath());
        ProcessUtil.execute(command);
        return thumbnail;
    }

    public String getVideoLength(File video) {
        String command = MessageFormat.format(FFPROBE_COMMAND,
                properties.getProcessorPath().getAbsolutePath(),
                properties.getInfoProvider(),
                video.getAbsolutePath());
        return ProcessUtil.execute(command);
    }

}
