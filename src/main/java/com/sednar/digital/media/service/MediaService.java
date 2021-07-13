package com.sednar.digital.media.service;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.file.FileSystem;
import com.sednar.digital.media.common.type.Quality;
import com.sednar.digital.media.common.type.Rating;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.exception.MediaException;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.resource.v1.model.MediaDto;
import com.sednar.digital.media.resource.v1.model.MediaRequest;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import com.sednar.digital.media.service.events.UploadEvent;
import com.sednar.digital.media.service.progress.ProgressService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaService {

    private final MediaRepository mediaRepository;

    private final ProgressService progressService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MediaService(MediaRepository mediaRepository,
                        ProgressService progressService,
                        ApplicationEventPublisher applicationEventPublisher) {
        this.mediaRepository = mediaRepository;
        this.progressService = progressService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<MediaDto> search(String searchText) {
        return search(null, searchText);
    }

    public List<MediaDto> search(Type type, String searchText) {
        List<Media> mediaList;
        if (type == null) {
            mediaList = mediaRepository.findAllByOrderByViewsDescLikesDesc();
        } else {
            mediaList = mediaRepository.findByTypeOrderByViewsDescLikesDesc(type.getCode());
        }
        List<MediaDto> list = MapperConstant.MEDIA.map(mediaList);
        return list.stream()
                .filter(m -> m.getTags().stream().anyMatch(searchText::equalsIgnoreCase)
                    || StringUtils.containsIgnoreCase(m.getName(), searchText))
                .collect(Collectors.toList());
    }

    // Mark @Transactional
    public ProgressDto upload(Type type, MediaRequest request, MultipartFile multipartFile)
            throws MediaException {
        try {
            String name = request.getName();
            long size = multipartFile.getSize();
            log.info("upload requested, type={}, name={}, size={}", type, name, size);
            if (type == null) {
                throw new MediaException("Invalid media type");
            }
            if (StringUtils.isBlank(name)) {
                throw new MediaException("Name is required");
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
            Media savedMedia = mediaRepository.save(media);

            String trackingId = UUID.randomUUID().toString();
            log.info("Assigned tracking trackingId={}, type={}, name={}, size={}", trackingId, type, name, size);
            File uploadedFile = FileSystem.save(trackingId, multipartFile.getBytes());
            log.info("Saved to local disk, trackingId={}", trackingId);
            ProgressDto savedProgress = progressService.save(trackingId, savedMedia.getId());
            applicationEventPublisher.publishEvent(
                    new UploadEvent(this, type, savedMedia.getId(), trackingId));
            return savedProgress;
        } catch(Exception e) {
            throw new MediaException("Unable to store " + type + ". ERROR=" + e.getMessage());
        }
    }

    public MediaDto update(Long id, MediaRequest request) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new MediaException("Invalid media id"));
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

}
