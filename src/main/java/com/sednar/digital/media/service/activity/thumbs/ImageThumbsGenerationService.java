package com.sednar.digital.media.service.activity.thumbs;

import com.sednar.digital.media.common.exception.MediaException;
import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class ImageThumbsGenerationService {

    private final ActivityProgressRepository activityProgressRepository;

    private final ImageRepository imageRepository;

    private final ImageContentProcessor imageContentProcessor;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public ImageThumbsGenerationService(
            ActivityProgressRepository activityProgressRepository,
            ImageRepository imageRepository,
            ImageContentProcessor imageContentProcessor,
            FileSystemClient fileSystemClient) {
        this.activityProgressRepository = activityProgressRepository;
        this.imageRepository = imageRepository;
        this.imageContentProcessor = imageContentProcessor;
        this.fileSystemClient = fileSystemClient;
    }

    void generateThumbs(List<Media> mediaList, ActivityProgress activityProgress,
                        GenerationStrategy strategy) {
        int success = 0;
        int skipped = 0;
        int failed = 0;
        log.info("There are {} images found for generating thumbnails", mediaList.size());
        for (Media media : mediaList) {
            Long id = media.getId();
            String stringId = String.valueOf(id);
            try {
                Image image = imageRepository.findById(id)
                        .orElseThrow(() -> new MediaException("Image not found with id " + id));
                if (strategy != GenerationStrategy.ONLY_ABSENT || image.getThumbnail() == null) {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                    continue;
                }
                File file = fileSystemClient.getMedia(Type.VIDEO, stringId);
                if (!file.exists()) {
                    file = fileSystemClient.createWorkingFile(stringId, image.getContent());
                }
                File thumbnail = imageContentProcessor.generateThumbnail(file);
                image.setThumbnail(FileUtils.readFileToByteArray(thumbnail));
                log.info("Generated thumbnail for the id={}", id);
                imageRepository.save(image);
                image.setThumbnail(null);
                // Save a copy to the file system
                fileSystemClient.store(Type.IMAGE, id, file, thumbnail);
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
