package com.sednar.digital.media.service.events;

import com.sednar.digital.media.service.handler.UploadRequest;
import com.sednar.digital.media.service.handler.chain.AbstractUploadHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UploadEventListener implements ApplicationListener<UploadEvent> {

    private final AbstractUploadHandler abstractUploadHandler;

    @Autowired
    UploadEventListener(AbstractUploadHandler abstractUploadHandler) {
        this.abstractUploadHandler = abstractUploadHandler;
    }

    @Override
    public void onApplicationEvent(UploadEvent uploadEvent) {
        String trackingId = uploadEvent.getTrackingId();
        log.info("Received Event, trackingId={}", trackingId);

        UploadRequest request = new UploadRequest(trackingId, uploadEvent.getMediaId());
        request.addAttribute(UploadRequest.WORKING_FILE, uploadEvent.getWorkingFile());
        abstractUploadHandler.handleRequest(request);
    }

}
