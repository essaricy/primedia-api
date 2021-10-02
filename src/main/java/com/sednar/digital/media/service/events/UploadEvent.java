package com.sednar.digital.media.service.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.File;

@Getter
public class UploadEvent extends ApplicationEvent {

    private final Long mediaId;

    private final String trackingId;

    private final File workingFile;

    public UploadEvent(Object source, String trackingId, Long mediaId, File workingFile) {
        super(source);
        this.mediaId = mediaId;
        this.trackingId = trackingId;
        this.workingFile = workingFile;
    }

}
