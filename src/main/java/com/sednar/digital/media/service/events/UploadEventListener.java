package com.sednar.digital.media.service.events;

import com.sednar.digital.media.common.file.FileSystem;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.service.content.MediaContentProcessor;
import com.sednar.digital.media.service.content.MediaContentProcessorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class UploadEventListener implements ApplicationListener<UploadEvent> {

    private final MediaContentProcessorFactory mediaContentProcessorFactory;

    @Autowired
    UploadEventListener(MediaContentProcessorFactory mediaContentProcessorFactory) {
        this.mediaContentProcessorFactory = mediaContentProcessorFactory;
    }
    @Override
    public void onApplicationEvent(UploadEvent uploadEvent) {
        Type type = uploadEvent.getType();
        Long mediaId = uploadEvent.getMediaId();
        String trackingId = uploadEvent.getTrackingId();
        log.info("Received Event, trackingId={}", trackingId);
        try {
            File uploadedFile = FileSystem.get(trackingId);
            log.info("Saved to local disk, trackingId={}", trackingId);
            MediaContentProcessor mediaContentProcessor = mediaContentProcessorFactory.getInstance(type);
            mediaContentProcessor.process(mediaId, trackingId, uploadedFile);
            log.info("Media content has been saved successfully for trackingId={}", trackingId);
        } catch (Exception e) {
            log.error("Error occurred while processing media content. trackingId={}", trackingId, e);
        }
    }

}
