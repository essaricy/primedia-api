package com.sednar.digital.media.service.events;

import com.sednar.digital.media.common.type.Type;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class UploadEvent extends ApplicationEvent {

    @Getter
    private final Type type;

    @Getter
    private final Long mediaId;

    @Getter
    private final String trackingId;

    public UploadEvent(Object source, Type type, Long mediaId, String trackingId) {
        super(source);
        this.type = type;
        this.mediaId = mediaId;
        this.trackingId = trackingId;
    }

}
