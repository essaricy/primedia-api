package com.sednar.digital.media.service.events.application;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.common.type.UploadStatus;
import com.sednar.digital.media.common.util.DurationUtil;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.UploadProgress;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.events.UploadEvent;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class UploadEventListener implements ApplicationListener<UploadEvent> {

    private final ImageContentProcessor imageContentProcessor;

    private final VideoContentProcessor videoContentProcessor;

    private final UploadProgressRepository uploadProgressRepository;

    private final MediaRepository mediaRepository;

    private final FileSystemClient fileSystemClient;

    @Autowired
    UploadEventListener(
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

    @Override
    public void onApplicationEvent(UploadEvent uploadEvent) {
        Media media = uploadEvent.getMedia();
        UploadProgress uploadProgress = uploadEvent.getUploadProgress();
        File workingFile = uploadEvent.getWorkingFile();
        Type type = Type.fromCode(media.getType());
        Long mediaId = uploadProgress.getMediaId();
        String trackingId = uploadProgress.getId();
        log.info("Received Event, trackingId={}", trackingId);

        double videoLength = 0;
        File thumb = null;

        // Generate Thumbnails
        try {
             if (type == Type.VIDEO) {
                videoLength = videoContentProcessor.getVideoLength(workingFile);
                log.info("Obtained video length, trackingId={}, length={}", trackingId, videoLength);
                thumb = videoContentProcessor.generateThumbnail(workingFile, videoLength);
            } else if (type == Type.IMAGE) {
                thumb = imageContentProcessor.generateThumbnail(workingFile);
            }
            log.info("Generated thumbnail, trackingId={}", trackingId);
            uploadProgressRepository.updateOnStep(uploadProgress, UploadStatus.THUMB_DONE);
        } catch (Exception e) {
            log.info("Generating thumbnail failed, trackingId={}, error={}", trackingId, e);
            uploadProgressRepository.updateOnException(uploadProgress, UploadStatus.THUMB_FAIL, e);
            deleteWorkingFiles(workingFile, thumb);
        }
        // Save Content
        try {
            if (type == Type.VIDEO) {
                videoContentProcessor.saveContent(mediaId, workingFile, thumb);
                media.setDuration(DurationUtil.getDurationStamp(videoLength));
                mediaRepository.save(media);
            } else if (type == Type.IMAGE) {
                imageContentProcessor.saveContent(mediaId, workingFile, thumb);
            }
            log.info("Saved to database successfully, trackingId={}", trackingId);
            uploadProgressRepository.updateOnStep(uploadProgress, UploadStatus.DB_DONE);
        } catch (Exception e) {
            log.info("Saving content/thumbnail to database failed, trackingId={}, error={}", trackingId, e);
            uploadProgressRepository.updateOnException(uploadProgress, UploadStatus.DB_FAIL, e);
            deleteWorkingFiles(workingFile, thumb);
        }
        // Copy file to file system storage
        try {
            fileSystemClient.store(type, mediaId, workingFile, thumb);
            log.info("Saved to file system successfully, trackingId={}", trackingId);
            uploadProgressRepository.updateOnStep(uploadProgress, UploadStatus.FILE_DONE);
        } catch (Exception e) {
            log.info("Saving content/thumbnail to file system failed, trackingId={}, error={}", trackingId, e);
            uploadProgressRepository.updateOnException(uploadProgress, UploadStatus.FILE_FAIL, e);
            deleteWorkingFiles(workingFile, thumb);
        }
        uploadProgressRepository.end(uploadProgress);
        deleteWorkingFiles(workingFile, thumb);
    }

    private void deleteWorkingFiles(File workingFile, File thumb) {
        FileUtils.deleteQuietly(workingFile);
        FileUtils.deleteQuietly(thumb);
    }

}
