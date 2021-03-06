package com.sednar.digital.media.service;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.exception.MediaException;
import com.sednar.digital.media.common.type.Quality;
import com.sednar.digital.media.common.type.Rating;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.common.type.UploadStatus;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.resource.model.MediaDto;
import com.sednar.digital.media.resource.model.MediaRequestDto;
import com.sednar.digital.media.resource.model.UploadProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import com.sednar.digital.media.service.events.UploadEvent;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaService {

    public static final int DEFAULT_MAX_RESULTS = 5;

    private final MediaRepository mediaRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public MediaService(MediaRepository mediaRepository,
                        FileSystemClient fileSystemClient,
                        ApplicationEventPublisher applicationEventPublisher) {
        this.mediaRepository = mediaRepository;
        this.fileSystemClient = fileSystemClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<MediaDto> search(Type type, String searchText) {
        if (StringUtils.isBlank(searchText)) {
            List<Media> mediaList = mediaRepository.findAllByTypeOrderByUploadDateDesc(type.getCode());
            return MapperConstant.MEDIA.map(mediaList);
        } else {
            List<Media> mediaList = mediaRepository.findByTypeOrderByViewsDescLikesDesc(type.getCode());
            List<Media> matches = new ArrayList<>();
            // First, match with the whole text provided
            matches.addAll(getMatches(searchText, mediaList, matches));

            // Then, match with the words provided
            List<String> acceptableWords = Arrays.stream(searchText.split("\\s+"))
                    .filter(w -> StringUtils.trim(w).length() >= 3)
                    .collect(Collectors.toList());
            acceptableWords.stream().forEach(word -> matches.addAll(getMatches(word, mediaList, matches)));
            return MapperConstant.MEDIA.map(matches);
        }
    }

    private List<Media> getMatches(String searchText, List<Media> mediaList, List<Media> matches) {
        return mediaList.stream()
                .filter(media -> StringUtils.containsIgnoreCase(media.getTags(), searchText)
                        || StringUtils.containsIgnoreCase(media.getName(), searchText))
                .filter(m -> matches.stream()
                        .noneMatch(match -> match.getId().longValue() == m.getId().longValue()))
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
            Long mediaId = mediaRepository.save(media).getId();

            applicationEventPublisher.publishEvent(
                    new UploadEvent(this, trackingId, mediaId, workingFile));
            return initUploadProgress(trackingId, mediaId);
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

    private UploadProgressDto initUploadProgress(String trackingId, long mediaId) {
        UploadProgressDto uploadProgress = new UploadProgressDto();
        uploadProgress.setId(trackingId);
        uploadProgress.setMediaId(mediaId);
        uploadProgress.setStartTime(new Timestamp(System.currentTimeMillis()));
        uploadProgress.setStatus(UploadStatus.INIT);
        return uploadProgress;
    }

}
