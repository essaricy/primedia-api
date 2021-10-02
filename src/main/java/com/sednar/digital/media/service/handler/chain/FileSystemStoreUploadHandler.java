package com.sednar.digital.media.service.handler.chain;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.common.type.UploadStatus;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import com.sednar.digital.media.service.handler.UploadRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class FileSystemStoreUploadHandler extends AbstractUploadHandler {

    public FileSystemStoreUploadHandler(
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor,
            UploadProgressRepository uploadProgressRepository,
            MediaRepository mediaRepository,
            FileSystemClient fileSystemClient) {
        super(imageContentProcessor, videoContentProcessor, uploadProgressRepository,
                mediaRepository, fileSystemClient);
    }

    @Override
    protected void preProcess(UploadRequest request) {
        request.addAttribute(UploadRequest.UPLOAD_STATUS_SUCCESS, UploadStatus.FILE_DONE);
        request.addAttribute(UploadRequest.UPLOAD_STATUS_FAILURE, UploadStatus.FILE_FAIL);
    }

    @Override
    protected void process(UploadRequest request) throws Exception {
        String trackingId = request.getTrackingId();
        log.info("Processing started, trackingId={}", trackingId);

        Media media = request.getMedia();
        Long mediaId = media.getId();
        File workingFile = request.getFile(UploadRequest.WORKING_FILE);
        File thumb = request.getFile(UploadRequest.THUMB_FILE);
        Type type = Type.fromCode(media.getType());

        // Copy file to file system storage
        fileSystemClient.store(type, mediaId, workingFile, thumb);
        log.info("Saved to file system successfully, trackingId={}", trackingId);
    }
}
