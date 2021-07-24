package com.sednar.digital.media.service.content.video;

import com.sednar.digital.media.common.util.ProcessUtil;
import com.sednar.digital.media.service.config.properties.VideoContentProcessingProps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.MessageFormat;

@Service
public class FFmpegVideoService {

    private static final String FFPROBE_COMMAND = "\"{0}\\{1}\" -loglevel error -of csv=p=0 -show_entries format=duration \"{2}\"";

    private static final String FFMPEG_GIF = "\"{0}\\{1}\" -ss {2} -t {3} -i \"{4}\" -vf \"fps=10,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse\" -loop 0 \"{5}\"";

    private final VideoContentProcessingProps properties;

    @Autowired
    FFmpegVideoService(VideoContentProcessingProps properties) {
        this.properties = properties;
    }

    File generateThumbnail(File file) {
        int videoLength = getVideoLength(file);
        int grabAtPercent = properties.getGrabAtPercent();
        int grabPosition = videoLength/(100/grabAtPercent);
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

    private int getVideoLength(File video) {
        String command = MessageFormat.format(FFPROBE_COMMAND,
                properties.getProcessorPath().getAbsolutePath(),
                properties.getInfoProvider(),
                video.getAbsolutePath());
        String output = ProcessUtil.execute(command);
        if (StringUtils.isEmpty(output)) {
            return 0;
        }
        return (int) Double.parseDouble(output);
    }

}
