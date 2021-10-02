package com.sednar.digital.media.service.handler.chain;

import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import com.sednar.digital.media.service.handler.UploadRequest;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;

@Slf4j
public class StartUploadHandler extends AbstractUploadHandler {

    public StartUploadHandler(
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor,
            UploadProgressRepository uploadProgressRepository,
            MediaRepository mediaRepository,
            FileSystemClient fileSystemClient) {
        super(imageContentProcessor, videoContentProcessor,
                uploadProgressRepository, mediaRepository, fileSystemClient);
    }

    @Override
    protected void preProcess(UploadRequest request) {}

    @Override
    protected void process(UploadRequest request) {
        String trackingId = request.getTrackingId();
        log.info("Processing started, trackingId={}", trackingId);
        request.addAttribute(UploadRequest.UPLOAD_PROGRESS,
                uploadProgressRepository.findById(trackingId)
                        .orElseThrow(() -> new ValidationException("Progress not saved")));
        request.addAttribute(UploadRequest.MEDIA,
                mediaRepository.findById(request.getMediaId())
                        .orElseThrow(() -> new ValidationException("Media not saved")));
    }

}
