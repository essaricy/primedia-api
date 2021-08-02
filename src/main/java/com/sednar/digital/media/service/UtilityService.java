package com.sednar.digital.media.service;

import com.sednar.digital.media.common.file.FileSystem;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.resource.v1.model.BatchDto;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.util.DurationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UtilityService {

    private final ImageRepository imageRepository;

    private final VideoRepository videoRepository;

    private final MediaRepository mediaRepository;

    @Autowired
    ImageContentProcessor imageContentProcessor;

    @Autowired
    VideoContentProcessor videoContentProcessor;

    @Autowired
    public UtilityService(ImageRepository imageRepository,
                          VideoRepository videoRepository,
                          MediaRepository mediaRepository) {
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.mediaRepository = mediaRepository;
    }

    public BatchDto generateThumbs(Type type) {
        List<Long> successList = new ArrayList<>();
        List<Long> failureList = new ArrayList<>();

        String sessionId = UUID.randomUUID().toString().substring(1, 8);
        if (type == Type.IMAGE) {
            List<Image> images = imageRepository.findAll();
            log.info("There are {} images found for generating thumbnails", images.size());
            images.stream().forEach(image -> {
                Long id = image.getId();
                try {
                    File file = FileSystem.save(getImageFileName(sessionId, id), image.getContent());
                    File thumbnail = imageContentProcessor.generateThumbnail(file);
                    image.setThumbnail(FileUtils.readFileToByteArray(thumbnail));
                    log.info("Generated thumbnail for the id={}", id);
                    deleteQuietly(file, thumbnail);
                    imageRepository.save(image);
                    log.info("Saved thumbnail for the id={}", id);
                    successList.add(id);
                } catch (Exception e) {
                    log.error("Unable to save the file with image id {}", id);
                    failureList.add(id);
                }
            });
        } else if (type == Type.VIDEO) {
            List<Video> videos = videoRepository.findAll();
            log.info("There are {} videos found for generating thumbnails", videos.size());
            videos.stream().forEach(video -> {
                Long id = video.getId();
                try {
                    File file = FileSystem.save(getVideoFileName(sessionId, id), video.getContent());
                    double length = videoContentProcessor.getVideoLength(file);
                    File thumbnail = videoContentProcessor.generateThumbnail(file, length);
                    video.setThumbnail(FileUtils.readFileToByteArray(thumbnail));
                    log.info("Generated thumbnail for the id={}", id);
                    deleteQuietly(file, thumbnail);
                    videoRepository.save(video);
                    log.info("Saved thumbnail for the video id={}", id);
                    successList.add(id);
                } catch (Exception e) {
                    log.error("Unable to save the file with id {}", id);
                    failureList.add(id);
                }
            });
        }
        return getBatchDto(successList, failureList);
    }

    public BatchDto generateDuration() {
        List<Long> successList = new ArrayList<>();
        List<Long> failureList = new ArrayList<>();

        String sessionId = UUID.randomUUID().toString().substring(1, 8);
        List<Media> mediaList = mediaRepository.findByType(Type.VIDEO.getCode());
        log.info("There are {} medias found for generating duration", mediaList.size());
        mediaList.stream().forEach(media -> {
            Long id = media.getId();
            try {
                Video video = videoRepository.findById(id)
                        .orElseThrow(() -> new ValidationException("No video found for the id " + id));
                File file = FileSystem.save(getVideoFileName(sessionId, id), video.getContent());
                double length = videoContentProcessor.getVideoLength(file);
                media.setDuration(DurationUtil.getDurationStamp(length));
                mediaRepository.save(media);
                log.info("Updated duration stamp for the id={}", id);
                deleteQuietly(file, file);
                successList.add(id);
            } catch (Exception e) {
                log.error("Unable to save the file with id={}", id);
                failureList.add(id);
            }
        });
        return getBatchDto(successList, failureList);
    }

    private void deleteQuietly(File... files) {
        for (File file : files) {
            FileUtils.deleteQuietly(file);
        }
    }

    private String getImageFileName(String sessionId, Long id) {
        return MessageFormat.format("{0}_{1}.jpg", sessionId, id);
    }

    private String getVideoFileName(String sessionId, Long id) {
        return MessageFormat.format("{0}_{1}", sessionId, id);
    }

    private BatchDto getBatchDto(List<Long> successList, List<Long> failureList) {
        return BatchDto.builder()
                .success(successList.size())
                .failed(failureList.size())
                .total(successList.size() + failureList.size())
                .successList(successList)
                .failureList(failureList)
                .build();
    }

}
