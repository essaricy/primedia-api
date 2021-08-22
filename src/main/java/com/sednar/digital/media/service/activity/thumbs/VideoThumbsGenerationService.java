package com.sednar.digital.media.service.activity.thumbs;

import com.sednar.digital.media.common.exception.MediaException;
import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class VideoThumbsGenerationService {

    private final ActivityProgressRepository activityProgressRepository;

    private final VideoRepository videoRepository;

    private final VideoContentProcessor videoContentProcessor;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public VideoThumbsGenerationService(
            ActivityProgressRepository activityProgressRepository,
            VideoRepository videoRepository,
            VideoContentProcessor videoContentProcessor,
            FileSystemClient fileSystemClient) {
        this.activityProgressRepository = activityProgressRepository;
        this.videoRepository = videoRepository;
        this.videoContentProcessor = videoContentProcessor;
        this.fileSystemClient = fileSystemClient;
    }

    void generateThumbs(List<Media> mediaList, ActivityProgress activityProgress,
                        GenerationStrategy strategy) {
        int success = 0;
        int skipped = 0;
        int failed = 0;
        log.info("There are {} videos found for generating thumbnails", mediaList.size());
        for (Media media : mediaList) {
            Long id = media.getId();
            String stringId = String.valueOf(id);
            try {
                Video video = videoRepository.findById(id)
                        .orElseThrow(() -> new MediaException("Video not found with id " + id));
                if (strategy != GenerationStrategy.ONLY_ABSENT || video.getThumbnail() == null) {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                    continue;
                }
                File file = fileSystemClient.getMedia(Type.VIDEO, stringId);
                if (!file.exists()) {
                    file = fileSystemClient.createWorkingFile(stringId, video.getContent());
                }
                double length = videoContentProcessor.getVideoLength(file);
                File thumbnail = videoContentProcessor.generateThumbnail(file, length);
                video.setThumbnail(FileUtils.readFileToByteArray(thumbnail));
                log.info("Generated thumbnail for the id={}", id);
                videoRepository.save(video);
                // Save a copy to the file system
                fileSystemClient.store(Type.VIDEO, id, file, thumbnail);
                log.info("Saved thumbnail for the id={}", id);
                activityProgressRepository.updateOnSuccess(++success, activityProgress);
            } catch (Exception e) {
                log.error("Unable to generate thumbnail for the id={}", id);
                activityProgressRepository.updateOnException(++failed, activityProgress);
            }
        }
        activityProgressRepository.end(activityProgress);
    }

}
