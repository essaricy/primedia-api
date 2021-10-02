package com.sednar.digital.media.service.handler.chain;

import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.repo.entity.UploadProgress;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import com.sednar.digital.media.service.handler.UploadRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public abstract class AbstractUploadHandler {

    protected ImageContentProcessor imageContentProcessor;

    protected VideoContentProcessor videoContentProcessor;

    protected UploadProgressRepository uploadProgressRepository;

    protected MediaRepository mediaRepository;

    protected FileSystemClient fileSystemClient;

    AbstractUploadHandler(
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor,
            UploadProgressRepository uploadProgressRepository,
            MediaRepository mediaRepository,
            FileSystemClient fileSystemClient) {
        this.imageContentProcessor = imageContentProcessor;
        this.videoContentProcessor = videoContentProcessor;
        this.uploadProgressRepository = uploadProgressRepository;
        this.mediaRepository = mediaRepository;
        this.fileSystemClient = fileSystemClient;
    }

    @Setter
    protected AbstractUploadHandler next;

    protected abstract void preProcess(UploadRequest request) throws Exception;

    protected abstract void process(UploadRequest request) throws Exception;

    public void handleRequest(UploadRequest request) {
        UploadProgress uploadProgress = request.getUploadProgress();
        try {
            preProcess(request);
            process(request);
            if (next != null) {
                Optional.ofNullable(request.getUploadStatus(UploadRequest.UPLOAD_STATUS_SUCCESS))
                        .ifPresent(s -> uploadProgressRepository.updateOnStep(uploadProgress, s));
                next.process(request);
            }
        } catch (Exception e) {
            String trackingId = request.getTrackingId();
            log.info("Error occurred while processing, trackingId={}, error={}", trackingId, e);
            Optional.ofNullable(request.getUploadStatus(UploadRequest.UPLOAD_STATUS_FAILURE))
                    .ifPresent(s -> uploadProgressRepository.updateOnException(uploadProgress, s, e));
            deleteWorkingFiles(request.getFile(UploadRequest.WORKING_FILE),
                    request.getFile(UploadRequest.THUMB_FILE));
        }
    }

    protected void deleteWorkingFiles(File... files) {
        Arrays.stream(files).forEach(FileUtils::deleteQuietly);
    }

}
