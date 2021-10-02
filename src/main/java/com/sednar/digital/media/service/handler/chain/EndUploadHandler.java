package com.sednar.digital.media.service.handler.chain;

import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.repo.entity.UploadProgress;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import com.sednar.digital.media.service.handler.UploadRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EndUploadHandler extends AbstractUploadHandler {

    public EndUploadHandler(
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
        UploadProgress uploadProgress = request.getUploadProgress();
        uploadProgressRepository.end(uploadProgress);
        deleteWorkingFiles(request.getFile(UploadRequest.WORKING_FILE),
                request.getFile(UploadRequest.THUMB_FILE));
    }

}
