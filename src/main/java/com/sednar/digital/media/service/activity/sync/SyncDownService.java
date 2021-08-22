package com.sednar.digital.media.service.activity.sync;

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
public class SyncDownService {

    private final ActivityProgressRepository activityProgressRepository;

    private final ImageRepository imageRepository;

    private final VideoRepository videoRepository;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public SyncDownService(
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
    public void sync(Type type, List<Media> mediaList, ActivityProgress activityProgress) {
        if (type == Type.IMAGE) {
            syncImages(mediaList, activityProgress);
        } else if (type == Type.VIDEO) {
            syncVideos(mediaList, activityProgress);
        }
    }

    void syncImages(List<Media> mediaList, ActivityProgress activityProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (Media media : mediaList) {
            Long mediaId = media.getId();
            String fileName = String.valueOf(mediaId);
            try {
                if (fileSystemClient.exists(Type.IMAGE, fileName)) {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                    continue;
                }
                Image image = imageRepository.findById(mediaId)
                        .orElseThrow(() -> new ValidationException("No image found for id " + mediaId));
                fileSystemClient.store(Type.IMAGE, fileName, image.getContent(), image.getThumbnail());
                activityProgressRepository.updateOnSuccess(++success, activityProgress);
                log.info("SyncDown successful for the media: : {}", mediaId);
            } catch (Exception e) {
                activityProgressRepository.updateOnException(++failed, activityProgress);
            }
        }
        activityProgressRepository.end(activityProgress);
    }

    void syncVideos(List<Media> mediaList, ActivityProgress activityProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (Media media : mediaList) {
            Long mediaId = media.getId();
            String fileName = String.valueOf(mediaId);
            try {
                if (fileSystemClient.exists(Type.VIDEO, fileName)) {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                } else {
                    Video video = videoRepository.findById(mediaId)
                            .orElseThrow(() -> new ValidationException("No video found for id " + mediaId));
                    fileSystemClient.store(Type.IMAGE, fileName, video.getContent(), video.getThumbnail());
                    activityProgressRepository.updateOnSuccess(++success, activityProgress);
                    log.info("SyncDown successful for the media: : {}", mediaId);
                }
            } catch (Exception e) {
                activityProgressRepository.updateOnException(++failed, activityProgress);
            }
        }
        activityProgressRepository.end(activityProgress);
    }

}
