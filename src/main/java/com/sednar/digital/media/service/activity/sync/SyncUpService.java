package com.sednar.digital.media.service.activity.sync;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.common.util.DurationUtil;
import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class SyncUpService {

    private final ActivityProgressRepository activityProgressRepository;

    private final MediaRepository mediaRepository;

    private final ImageContentProcessor imageContentProcessor;

    private final VideoContentProcessor videoContentProcessor;

    @Autowired
    public SyncUpService(
            ActivityProgressRepository activityProgressRepository,
            MediaRepository mediaRepository,
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor) {
        this.activityProgressRepository = activityProgressRepository;
        this.mediaRepository = mediaRepository;
        this.imageContentProcessor = imageContentProcessor;
        this.videoContentProcessor = videoContentProcessor;
    }

    @Async
    public void sync(Type type, Collection<File> files, ActivityProgress activityProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (File file : files) {
            Long id = Long.parseLong(file.getName());
            Optional<Media> mediaOptional = mediaRepository.findById(id);
            try {
                if (mediaOptional.isPresent()) {
                    generateAndSave(type, file, mediaOptional.get(), id);
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

    private void generateAndSave(Type type, File file, Media media, Long id)
            throws IOException {
        if (type == Type.IMAGE) {
            File thumb = imageContentProcessor.generateThumbnail(file);
            imageContentProcessor.saveContent(id, file, thumb);
        } else if (type == Type.VIDEO) {
            double videoLength = videoContentProcessor.getVideoLength(file);
            File thumb = videoContentProcessor.generateThumbnail(file, videoLength);
            videoContentProcessor.saveContent(id, file, thumb);
            media.setDuration(DurationUtil.getDurationStamp(videoLength));
            mediaRepository.save(media);
        }
    }

}
