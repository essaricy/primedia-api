package com.sednar.digital.media.service.events.application;

import com.sednar.digital.media.common.exception.MediaException;
import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.filesystem.FileSystemClient;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Progress;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.events.UploadEvent;
import com.sednar.digital.media.util.DurationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Timestamp;

@Component
@Slf4j
public class UploadEventListener implements ApplicationListener<UploadEvent> {

    private final ImageContentProcessor imageContentProcessor;

    private final VideoContentProcessor videoContentProcessor;

    private final ProgressRepository progressRepository;

    private final MediaRepository mediaRepository;

    private final FileSystemClient fileSystemClient;

    @Autowired
    UploadEventListener(
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor,
            ProgressRepository progressRepository,
            MediaRepository mediaRepository,
            FileSystemClient fileSystemClient) {
        this.imageContentProcessor = imageContentProcessor;
        this.videoContentProcessor = videoContentProcessor;
        this.progressRepository = progressRepository;
        this.mediaRepository = mediaRepository;
        this.fileSystemClient = fileSystemClient;
    }

    @Override
    public void onApplicationEvent(UploadEvent uploadEvent) {
        Media media = uploadEvent.getMedia();
        Progress progress = uploadEvent.getProgress();
        File workingFile = uploadEvent.getWorkingFile();
        Type type = Type.fromCode(media.getType());
        Long mediaId = progress.getMediaId();
        String trackingId = progress.getId();
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
            setProcessStatus(progress, ProgressStatus.THUMB_DONE);
        } catch (Exception e) {
            log.info("Generating thumbnail failed, trackingId={}, error={}", trackingId, e);
            updateOnException(progress, ProgressStatus.THUMB_FAIL, e, workingFile, thumb);
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
            setProcessStatus(progress, ProgressStatus.DB_DONE);
        } catch (Exception e) {
            log.info("Saving content/thumbnail to database failed, trackingId={}, error={}", trackingId, e);
            updateOnException(progress, ProgressStatus.DB_FAIL, e, workingFile, thumb);
        }
        // Copy file to file system storage
        try {
            fileSystemClient.store(type, mediaId, workingFile, thumb);
            log.info("Saved to file system successfully, trackingId={}", trackingId);
            setProcessStatus(progress, ProgressStatus.FILE_DONE);
        } catch (Exception e) {
            log.info("Saving content/thumbnail to file system failed, trackingId={}, error={}", trackingId, e);
            updateOnException(progress, ProgressStatus.FILE_FAIL, e, workingFile, thumb);
        }
        progress.setEndTime(new Timestamp(System.currentTimeMillis()));
        setProcessStatus(progress, ProgressStatus.ALL_DONE);
        deleteWorkingFiles(workingFile, thumb);
    }

    private void updateOnException(
            Progress progress, ProgressStatus status, Exception e, File workingFile, File thumb) {
        setProcessStatus(progress, status, e.getMessage());
        deleteWorkingFiles(workingFile, thumb);
        throw new MediaException(e.getMessage(), e);
    }

    private void deleteWorkingFiles(File workingFile, File thumb) {
        FileUtils.deleteQuietly(workingFile);
        FileUtils.deleteQuietly(thumb);
    }

    private void setProcessStatus(Progress progress, ProgressStatus status) {
        setProcessStatus(progress, status, null);
    }

    private void setProcessStatus(Progress progress, ProgressStatus status, String errorMessage) {
        progress.setStatus(status.getCode());
        progress.setErrorMessage(errorMessage);
        progressRepository.save(progress);
        log.info("Updated process status of trackingId={} to {}", progress.getId(), status);
    }

}
