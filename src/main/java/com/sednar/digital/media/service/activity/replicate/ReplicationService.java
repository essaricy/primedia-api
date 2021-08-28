package com.sednar.digital.media.service.activity.replicate;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
public class ReplicationService {

    private final ActivityProgressRepository activityProgressRepository;

    private final ImageRepository imageRepository;

    private final VideoRepository videoRepository;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public ReplicationService(
            ActivityProgressRepository activityProgressRepository,
            ImageRepository imageRepository,
            VideoRepository videoRepository,
            FileSystemClient fileSystemClient) {
        this.activityProgressRepository = activityProgressRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.fileSystemClient = fileSystemClient;
    }

    @Async
    public void replicate(Type type, List<Media> mediaList, ActivityProgress activityProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (Media media : mediaList) {
            Long mediaId = media.getId();
            String fileName = media.getName() + "-" + mediaId;
            try {
                if (fileSystemClient.isDownloaded(type, fileName)) {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                    continue;
                }
                fileSystemClient.download(type, fileName, getContent(type, mediaId));
                activityProgressRepository.updateOnSuccess(++success, activityProgress);
                log.info("Replicate successful for the media: {}", mediaId);
            } catch (Exception e) {
                log.error("Replicate failed for media: " + mediaId, e);
                activityProgressRepository.updateOnException(++failed, activityProgress);
            }
        }
        activityProgressRepository.end(activityProgress);
    }

    private byte[] getContent(Type type, Long mediaId) {
        byte[] content = null;
        if (type == Type.IMAGE) {
            Image image = imageRepository.findById(mediaId)
                    .orElseThrow(() -> new ValidationException("No image found for id " + mediaId));
            content = image.getContent();
        } else if (type == Type.VIDEO) {
            Video video = videoRepository.findById(mediaId)
                    .orElseThrow(() -> new ValidationException("No video found for id " + mediaId));
            content = video.getContent();
        }
        return content;
    }

}
