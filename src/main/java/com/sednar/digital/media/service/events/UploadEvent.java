package com.sednar.digital.media.service.events;

import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.UploadProgress;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.File;

public class UploadEvent extends ApplicationEvent {

    @Getter
    private final Media media;

    @Getter
    private final UploadProgress uploadProgress;

    @Getter
    private final File workingFile;

    public UploadEvent(Object source, Media media, UploadProgress uploadProgress, File workingFile) {
        super(source);
        this.media = media;
        this.uploadProgress = uploadProgress;
        this.workingFile = workingFile;
    }

}
