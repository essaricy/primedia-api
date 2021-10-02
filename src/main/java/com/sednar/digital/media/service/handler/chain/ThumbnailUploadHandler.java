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
public class ThumbnailUploadHandler extends AbstractUploadHandler {

    public ThumbnailUploadHandler(
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor,
            UploadProgressRepository uploadProgressRepository,
            MediaRepository mediaRepository,
            FileSystemClient fileSystemClient) {
        super(imageContentProcessor, videoContentProcessor,
                uploadProgressRepository, mediaRepository, fileSystemClient);
    }

    @Override
    protected void preProcess(UploadRequest request) {
        request.addAttribute(UploadRequest.UPLOAD_STATUS_SUCCESS, UploadStatus.THUMB_DONE);
        request.addAttribute(UploadRequest.UPLOAD_STATUS_FAILURE, UploadStatus.THUMB_FAIL);
    }

    @Override
    protected void process(UploadRequest request) throws Exception {
        String trackingId = request.getTrackingId();
        log.info("Processing started, trackingId={}", trackingId);
        Media media = request.getMedia();
        File workingFile = request.getFile(UploadRequest.WORKING_FILE);
        Type type = Type.fromCode(media.getType());

        File thumb = null;
        if (type == Type.VIDEO) {
            double videoLength = videoContentProcessor.getVideoLength(workingFile);
            request.addAttribute(UploadRequest.LENGTH, videoLength);
            log.info("Obtained video length, trackingId={}, length={}", trackingId, videoLength);
            thumb = videoContentProcessor.generateThumbnail(workingFile, videoLength);
        } else if (type == Type.IMAGE) {
            thumb = imageContentProcessor.generateThumbnail(workingFile);
        }
        request.addAttribute(UploadRequest.THUMB_FILE, thumb);
        log.info("Generated thumbnail, trackingId={}", trackingId);
    }

}
