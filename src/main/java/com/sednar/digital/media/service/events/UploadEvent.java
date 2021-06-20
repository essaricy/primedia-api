package com.sednar.digital.media.service.events;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.MediaRequest;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.multipart.MultipartFile;

public class UploadEvent extends ApplicationEvent {

    @Getter
    private Type type;

    @Getter
    private Long mediaId;

    @Getter
    private String trackingId;

    @Getter
    private MultipartFile multipartFile;

    public UploadEvent(Object source, Type type, Long mediaId, String trackingId, MultipartFile multipartFile) {
        super(source);
        this.type = type;
        this.mediaId = mediaId;
        this.trackingId = trackingId;
        this.multipartFile = multipartFile;
    }

}
