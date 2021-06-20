package com.sednar.digital.media.service.content;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.exception.MediaException;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MediaContentProcessorFactory {

    private final ImageContentProcessor imageContentProcessor;

    private final VideoContentProcessor videoContentProcessor;

    @Autowired
    MediaContentProcessorFactory(ImageContentProcessor imageContentProcessor,
                                 VideoContentProcessor videoContentProcessor) {
        this.imageContentProcessor = imageContentProcessor;
        this.videoContentProcessor = videoContentProcessor;
    }

    public MediaContentProcessor getInstance(Type type) {
        if (type == Type.VIDEO) {
            return videoContentProcessor;
        } else if (type == Type.IMAGE) {
            return imageContentProcessor;
        }
        throw new MediaException("No MediaContentProcessor found for type=" + type);
    }

}
