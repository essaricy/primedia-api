package com.sednar.digital.media.service.activity.sync;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.common.util.DurationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class SyncUpService {

    private final ActivityProgressRepository activityProgressRepository;

    private final MediaRepository mediaRepository;

    private final ImageRepository imageRepository;

    private final VideoRepository videoRepository;

    @Autowired
    private FileSystemClient fileSystemClient;

    @Autowired
    private ImageContentProcessor imageContentProcessor;

    @Autowired
    private VideoContentProcessor videoContentProcessor;

    @Autowired
    public SyncUpService(
            ActivityProgressRepository activityProgressRepository,
            MediaRepository mediaRepository,
            ImageRepository imageRepository,
            VideoRepository videoRepository) {
        this.activityProgressRepository = activityProgressRepository;
        this.mediaRepository = mediaRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
    }

    @Async
    public void sync(Type type, Collection<File> files, ActivityProgress activityProgress) {
        if (type == Type.IMAGE) {
            syncImages(files, activityProgress);
        } else if (type == Type.VIDEO) {
            syncVideos(files, activityProgress);
        }
    }

    void syncImages(Collection<File> files, ActivityProgress activityProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (File file : files) {
            Long id = Long.parseLong(file.getName());
            Optional<Media> mediaOptional = mediaRepository.findById(id);
            try {
                if (mediaOptional.isPresent()) {
                    File thumb = imageContentProcessor.generateThumbnail(file);
                    imageContentProcessor.saveContent(id, file, thumb);
                    activityProgressRepository.updateOnSuccess(++success, activityProgress);
                } else {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                }
            } catch (Exception e) {
                activityProgressRepository.updateOnException(++failed, activityProgress);
            }
        }
        activityProgressRepository.end(activityProgress);
    }

    void syncVideos(Collection<File> files, ActivityProgress activityProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (File file : files) {
            Long id = Long.parseLong(file.getName());
            Optional<Media> mediaOptional = mediaRepository.findById(id);
            try {
                if (mediaOptional.isPresent()) {
                    Media media = mediaOptional.get();
                    Thread.sleep(2000);
                    double videoLength = videoContentProcessor.getVideoLength(file);
                    File thumb = videoContentProcessor.generateThumbnail(file, videoLength);
                    videoContentProcessor.saveContent(id, file, thumb);
                    media.setDuration(DurationUtil.getDurationStamp(videoLength));
                    mediaRepository.save(media);
                    activityProgressRepository.updateOnSuccess(++success, activityProgress);
                } else {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                }
            } catch (Exception e) {
                activityProgressRepository.updateOnException(++failed, activityProgress);
            }
        }
        activityProgressRepository.end(activityProgress);
    }

}
