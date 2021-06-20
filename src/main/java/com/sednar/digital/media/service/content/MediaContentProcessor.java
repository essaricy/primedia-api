package com.sednar.digital.media.service.content;

import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.entity.MediaContent;
import com.sednar.digital.media.repo.entity.Progress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@Slf4j
public abstract class MediaContentProcessor {

    private final ProgressRepository progressRepository;

    @Autowired
    public MediaContentProcessor(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    protected abstract File generateThumbnail(File uploadedFile) throws Exception;

    protected abstract MediaContent saveContent(Long mediaId, File content, File thumbnail) throws Exception;

    public void process(Long mediaId, String trackingId, File uploadedFile) throws Exception {
        File thumb = null;
        Progress progress = getProgress(trackingId);
        setProcessStatus(progress, ProgressStatus.PROCESS_STARTED);
        try {
            thumb = generateThumbnail(uploadedFile);
            setProcessStatus(progress, ProgressStatus.THUMBNAIL_GENERATED);
        } catch (Exception e) {
            setProcessStatus(progress, ProgressStatus.THUMBNAIL_FAILED);
            FileUtils.deleteQuietly(uploadedFile);
            FileUtils.deleteQuietly(thumb);
            throw new Exception(e);
        }
        try {
            saveContent(mediaId, uploadedFile, thumb);
            setProcessStatus(progress, ProgressStatus.SAVE_DONE);
        } catch (Exception e) {
            setProcessStatus(progress, ProgressStatus.SAVE_FAIL);
        }
        FileUtils.deleteQuietly(uploadedFile);
        FileUtils.deleteQuietly(thumb);
    }

    protected Progress getProgress(String trackingId) {
        return progressRepository.findById(trackingId)
                .orElseThrow(() -> new RuntimeException("Invalid progress id"));
    }

    protected void setProcessStatus(Progress progress, ProgressStatus status) {
        progress.setStatus(status.getCode());
        progressRepository.save(progress);
        log.info("Updated process status of trackingId={} to {}", progress.getId(), status);
    }

}
