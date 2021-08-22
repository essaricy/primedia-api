package com.sednar.digital.media.service.activity.duration;

import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.common.util.DurationUtil;
import com.sednar.digital.media.common.util.FileUtil;
import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.selectors.ContentColumn;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class DurationsGenerationService {

    private final ActivityProgressRepository activityProgressRepository;

    private final MediaRepository mediaRepository;

    private final VideoRepository videoRepository;

    private final VideoContentProcessor videoContentProcessor;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public DurationsGenerationService(
            ActivityProgressRepository activityProgressRepository,
            MediaRepository mediaRepository,
            VideoRepository videoRepository,
            VideoContentProcessor videoContentProcessor,
            FileSystemClient fileSystemClient) {
        this.activityProgressRepository = activityProgressRepository;
        this.mediaRepository = mediaRepository;
        this.videoRepository = videoRepository;
        this.videoContentProcessor = videoContentProcessor;
        this.fileSystemClient = fileSystemClient;
    }

    @Async
    public void generateDurations(List<Media> mediaList,
                                  ActivityProgress activityProgress,
                                  GenerationStrategy strategy) {
        int success = 0;
        int skipped = 0;
        int failed = 0;
        log.info("There are {} medias found for generating duration", mediaList.size());
        for (Media media : mediaList) {
            Long id = media.getId();
            String stringId = String.valueOf(id);
            try {
                if (strategy != GenerationStrategy.ONLY_ABSENT || StringUtils.isBlank(media.getDuration())) {
                    activityProgressRepository.updateOnSkipped(++skipped, activityProgress);
                    continue;
                }
                // Check if the file exists in file system. If not retrieve from the database
                boolean clearFile = false;
                File file = fileSystemClient.getMedia(Type.VIDEO, stringId);
                if (!file.exists()) {
                    ContentColumn contentColumn = videoRepository.readById(id);
                    file = fileSystemClient.createWorkingFile(stringId, contentColumn.getContent());
                    clearFile = true;
                }
                double length = videoContentProcessor.getVideoLength(file);
                media.setDuration(DurationUtil.getDurationStamp(length));
                mediaRepository.save(media);
                if (clearFile) {
                    FileUtil.deleteQuietly(file, file);
                }
                log.info("Updated duration stamp for the id={}", id);
                activityProgressRepository.updateOnSuccess(++success, activityProgress);
            } catch (Exception e) {
                log.error("Unable to save the file with id={}", id);
                activityProgressRepository.updateOnException(++failed, activityProgress);
            }
        }
        activityProgressRepository.end(activityProgress);
    }

}
