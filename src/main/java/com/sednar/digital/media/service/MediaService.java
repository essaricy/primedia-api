package com.sednar.digital.media.service;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.exception.MediaException;
import com.sednar.digital.media.common.type.Quality;
import com.sednar.digital.media.common.type.Rating;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.UploadProgress;
import com.sednar.digital.media.resource.v1.model.MediaDto;
import com.sednar.digital.media.resource.v1.model.MediaRequestDto;
import com.sednar.digital.media.resource.v1.model.UploadProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import com.sednar.digital.media.service.events.UploadEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaService {

    public static final int DEFAULT_MAX_RESULTS = 3;

    private final MediaRepository mediaRepository;

    private final UploadProgressRepository uploadProgressRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public MediaService(MediaRepository mediaRepository,
                        UploadProgressRepository uploadProgressRepository,
                        FileSystemClient fileSystemClient,
                        ApplicationEventPublisher applicationEventPublisher) {
        this.mediaRepository = mediaRepository;
        this.uploadProgressRepository = uploadProgressRepository;
        this.fileSystemClient = fileSystemClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<MediaDto> search(Type type, String searchText) {
        List<Media> mediaList = mediaRepository.findByTypeOrderByViewsDescLikesDesc(type.getCode());
        List<MediaDto> list = MapperConstant.MEDIA.map(mediaList);
        return list.stream()
                .filter(m -> m.getTags().stream().anyMatch(searchText::equalsIgnoreCase)
                    || StringUtils.containsIgnoreCase(m.getName(), searchText))
                .collect(Collectors.toList());
    }

    public UploadProgressDto upload(Type type, MediaRequestDto request, MultipartFile multipartFile)
            throws MediaException {
        try {
            String name = request.getName();
            long size = multipartFile.getSize();
            log.info("upload requested, type={}, name={}, size={}", type, name, size);
            if (type == null) {
                throw new ValidationException("Invalid media type");
            }
            if (StringUtils.isBlank(name)) {
                throw new ValidationException("Name is required");
            }
            Media media = new Media();
            media.setType(type.getCode());
            media.setName(name);
            media.setSize(size);
            media.setUploadDate(new Timestamp(System.currentTimeMillis()));

            Rating rating = request.getRating();
            if (rating != null) {
                media.setRating(rating.getCode());
            }
            Quality quality = request.getQuality();
            if (quality != null) {
                media.setQuality(quality.getCode());
            }
            Set<String> tags = request.getTags();
            if (CollectionUtils.isNotEmpty(tags)) {
                media.setTags(String.join(MediaConstants.TAG_SEPARATOR, tags));
            }
            String trackingId = UUID.randomUUID().toString();
            log.info("Assigned tracking trackingId={}, type={}, name={}, size={}", trackingId, type, name, size);
            File workingFile = fileSystemClient.createWorkingFile(trackingId, multipartFile.getBytes());
            Media savedMedia = mediaRepository.save(media);
            UploadProgress savedUploadProgress = uploadProgressRepository.initiate(trackingId, savedMedia.getId());
            applicationEventPublisher.publishEvent(
                    new UploadEvent(this, savedMedia, savedUploadProgress, workingFile));
            return MapperConstant.UPLOAD_PROGRESS.map(savedUploadProgress);
        } catch(Exception e) {
            throw new MediaException("Unable to store " + type + ". ERROR=" + e.getMessage());
        }
    }

    public MediaDto update(Long id, MediaRequestDto request) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new ValidationException("Invalid media id"));
        String name = request.getName();
        if (StringUtils.isNotBlank(name)) {
            media.setName(name);
        }
        Rating rating = request.getRating();
        if (rating != null) {
            media.setRating(rating.getCode());
        }
        Quality quality = request.getQuality();
        if (quality != null) {
            media.setQuality(quality.getCode());
        }
        Set<String> tags = request.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            media.setTags(String.join(MediaConstants.TAG_SEPARATOR, tags));
        }
        if (request.isAddView()) {
            media.setViews(media.getViews() + 1);
            media.setLastSeen(new Timestamp(System.currentTimeMillis()));
        }
        if (request.isAddLike()) {
            media.setLikes(media.getLikes() + 1);
        }
        Media savedMedia = mediaRepository.save(media);
        return MapperConstant.MEDIA.map(savedMedia);
    }

    public List<MediaDto> getAll(Type type) {
        return MapperConstant.MEDIA.map(mediaRepository.findByType(type.getCode()));
    }

    public List<MediaDto> getMostRecent(Type type, Integer max) {
        return MapperConstant.MEDIA.map(mediaRepository.findAllByTypeOrderByUploadDateDesc(type.getCode()))
                .stream()
                .limit(Optional.ofNullable(max).orElse(DEFAULT_MAX_RESULTS))
                .collect(Collectors.toList());
    }

    public List<MediaDto> getMostViewed(Type type, Integer max) {
        return MapperConstant.MEDIA.map(mediaRepository.findAllByTypeOrderByViewsDesc(type.getCode()))
                .stream()
                .filter(m -> m.getViews() != 0)
                .limit(Optional.ofNullable(max).orElse(DEFAULT_MAX_RESULTS))
                .collect(Collectors.toList());
    }

    public List<MediaDto> getMostLiked(Type type, Integer max) {
        return MapperConstant.MEDIA.map(mediaRepository.findAllByTypeOrderByLikesDesc(type.getCode()))
                .stream()
                .filter(m -> m.getLikes() != 0)
                .limit(Optional.ofNullable(max).orElse(DEFAULT_MAX_RESULTS))
                .collect(Collectors.toList());
    }

    public List<MediaDto> getMostRated(Type type, Integer max) {
        return MapperConstant.MEDIA.map(mediaRepository.findAllByTypeOrderByRatingDesc(type.getCode()))
                .stream()
                .limit(Optional.ofNullable(max).orElse(DEFAULT_MAX_RESULTS))
                .collect(Collectors.toList());
    }

}
