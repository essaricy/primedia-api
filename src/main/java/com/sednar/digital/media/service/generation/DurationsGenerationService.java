package com.sednar.digital.media.service.generation;

import com.sednar.digital.media.common.file.FileSystem;
import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.resource.v1.model.BatchDto;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.util.BatchResultUtil;
import com.sednar.digital.media.util.DurationUtil;
import com.sednar.digital.media.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DurationsGenerationService {

    private final VideoRepository videoRepository;

    private final MediaRepository mediaRepository;

    @Autowired
    ImageContentProcessor imageContentProcessor;

    @Autowired
    VideoContentProcessor videoContentProcessor;

    @Autowired
    public DurationsGenerationService(VideoRepository videoRepository, MediaRepository mediaRepository) {
        this.videoRepository = videoRepository;
        this.mediaRepository = mediaRepository;
    }

    public BatchDto generateDurations(GenerationStrategy strategy) {
        List<Long> successList = new ArrayList<>();
        List<Long> failureList = new ArrayList<>();

        String sessionId = UUID.randomUUID().toString().substring(1, 8);
        List<Media> mediaList = mediaRepository.findByType(Type.VIDEO.getCode());
        log.info("There are {} medias found for generating duration", mediaList.size());
        mediaList.stream()
            .filter(i -> strategy != GenerationStrategy.ONLY_ABSENT || StringUtils.isBlank(i.getDuration()))
            .forEach(media -> {
            Long id = media.getId();
            try {
                Video video = videoRepository.findById(id)
                        .orElseThrow(() -> new ValidationException("No video found for the id " + id));
                File file = FileSystem.save(FileUtil.getVideoFileName(sessionId, id), video.getContent());
                double length = videoContentProcessor.getVideoLength(file);
                media.setDuration(DurationUtil.getDurationStamp(length));
                mediaRepository.save(media);
                log.info("Updated duration stamp for the id={}", id);
                FileUtil.deleteQuietly(file, file);
                successList.add(id);
            } catch (Exception e) {
                log.error("Unable to save the file with id={}", id);
                failureList.add(id);
            }
        });
        return BatchResultUtil.getBatchDto(successList, failureList);
    }

}
