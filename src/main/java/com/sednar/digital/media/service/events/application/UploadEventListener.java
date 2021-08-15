package com.sednar.digital.media.service.events.application;

import com.sednar.digital.media.common.file.FileSystem;
import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.common.exception.MediaException;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Progress;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.events.UploadEvent;
import com.sednar.digital.media.util.DurationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.io.File;
import java.sql.Timestamp;

@Component
@Slf4j
public class UploadEventListener implements ApplicationListener<UploadEvent> {

    private final ImageContentProcessor imageContentProcessor;

    private final VideoContentProcessor videoContentProcessor;

    private final ProgressRepository progressRepository;

    private final MediaRepository mediaRepository;

    @Autowired
    UploadEventListener(
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor,
            ProgressRepository progressRepository,
            MediaRepository mediaRepository) {
        this.imageContentProcessor = imageContentProcessor;
        this.videoContentProcessor = videoContentProcessor;
        this.progressRepository = progressRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public void onApplicationEvent(UploadEvent uploadEvent) {
        Type type = uploadEvent.getType();
        Long mediaId = uploadEvent.getMediaId();
        String trackingId = uploadEvent.getTrackingId();
        log.info("Received Event, trackingId={}", trackingId);

        File uploadedFile = FileSystem.get(trackingId);
        log.info("Started processing the file, trackingId={}", trackingId);

        Progress progress = progressRepository.findById(trackingId)
                .orElseThrow(() -> new ValidationException("Invalid tracking id: " + trackingId));

        setProcessStatus(progress, ProgressStatus.PROCESS_STARTED);
        File thumb = null;
        double videoLength = 0;
        try {
            if (type == Type.VIDEO) {
                videoLength = videoContentProcessor.getVideoLength(uploadedFile);
                log.info("Obtained video length, trackingId={}, length={}", trackingId, videoLength);
                thumb = videoContentProcessor.generateThumbnail(uploadedFile, videoLength);
            } else if (type == Type.IMAGE) {
                thumb = imageContentProcessor.generateThumbnail(uploadedFile);
            }
            log.info("Generated thumbnail, trackingId={}", trackingId);
            setProcessStatus(progress, ProgressStatus.THUMBNAIL_GENERATED);
        } catch (Exception e) {
            log.info("Generating thumbnail failed, trackingId={}, error={}", trackingId, e);
            setProcessStatus(progress, ProgressStatus.THUMBNAIL_FAILED, e.getMessage());
            FileUtils.deleteQuietly(uploadedFile);
            FileUtils.deleteQuietly(thumb);
            throw new MediaException(e.getMessage(), e);
        }
        try {
            if (type == Type.VIDEO) {
                videoContentProcessor.saveContent(mediaId, uploadedFile, thumb);
                Media media = mediaRepository.findById(mediaId)
                        .orElseThrow(() -> new ValidationException("Invalid media id"));
                media.setDuration(DurationUtil.getDurationStamp(videoLength));
                mediaRepository.save(media);
            } else if (type == Type.IMAGE) {
                imageContentProcessor.saveContent(mediaId, uploadedFile, thumb);
            }
            log.info("Saved all content, trackingId={}", trackingId);
            progress.setEndTime(new Timestamp(System.currentTimeMillis()));
            setProcessStatus(progress, ProgressStatus.SAVE_DONE);
        } catch (Exception e) {
            log.info("Saving all content failed, trackingId={}, error={}", trackingId, e);
            setProcessStatus(progress, ProgressStatus.SAVE_FAIL, e.getMessage());
        }
        FileUtils.deleteQuietly(uploadedFile);
        FileUtils.deleteQuietly(thumb);
    }

    private void setProcessStatus(Progress progress, ProgressStatus status) {
        setProcessStatus(progress, status, null);
    }

    private void setProcessStatus(Progress progress, ProgressStatus status, String errorMessage) {
        progress.setStatus(status.getCode());
        progress.setErrorMessage(errorMessage);
        Progress savedProgress = progressRepository.save(progress);
        ProgressDto progressDto = MapperConstant.PROGRESS.map(savedProgress);
        log.info("Updated process status of trackingId={} to {}", progress.getId(), status);
    }

}
