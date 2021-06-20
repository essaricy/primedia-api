package com.sednar.digital.media.service;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.common.type.Quality;
import com.sednar.digital.media.common.type.Rating;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.exception.MediaException;
import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.MediaContent;
import com.sednar.digital.media.repo.entity.Progress;
import com.sednar.digital.media.resource.v1.model.MediaDto;
import com.sednar.digital.media.resource.v1.model.MediaRequest;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import com.sednar.digital.media.service.events.UploadEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MediaService {

    private final MediaRepository mediaRepository;

    private final ImageRepository imageRepository;

    private final VideoRepository videoRepository;

    private final ProgressRepository progressRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MediaService(MediaRepository mediaRepository,
                        ImageRepository imageRepository,
                        VideoRepository videoRepository,
                        ProgressRepository progressRepository) {
        this.mediaRepository = mediaRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.progressRepository = progressRepository;
    }

    public List<MediaDto> search(String searchText) {
        return search(null, searchText);
    }

    public List<MediaDto> search(Type type, String searchText) {
        List<Media> mediaList;
        if (type == null) {
            mediaList = mediaRepository.findAll();
        } else {
            mediaList = mediaRepository.findByType(type.getCode());
        }
        List<MediaDto> list = MapperConstant.MEDIA.map(mediaList);
        List<MediaDto> searchResult = list.stream()
                .filter(m -> m.getTags().stream().anyMatch(searchText::equalsIgnoreCase)
                    || StringUtils.containsIgnoreCase(m.getName(), searchText))
                .collect(Collectors.toList());
        return searchResult;
    }

    public byte[] getThumbnail(Type type, long id) {
        MediaContent mediaContent = getMediaContent(type, id);
        return mediaContent == null ? null : mediaContent.getThumbnail();
    }

    public byte[] getContent(Type type, long id) {
        MediaContent mediaContent = getMediaContent(type, id);
        return mediaContent == null ? null : mediaContent.getContent();
    }

    public ProgressDto upload(Type type, MediaRequest request, MultipartFile multipartFile)
            throws MediaException {
        try {
            String name = request.getName();
            long size = request.getSize();
            log.info("upload requested, type={}, name={}, size={}", type, name, size);
            if (type == null) {
                throw new MediaException("Invalid media type");
            }
            if (StringUtils.isBlank(name)) {
                throw new MediaException("Name is required");
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Media media = new Media();
            media.setType(type.getCode());
            media.setName(name);
            media.setSize(size);
            media.setUploadDate(timestamp);

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
            Progress progress = new Progress();
            String trackingId = UUID.randomUUID().toString();
            log.info("Assigned tracking trackingId={}, type={}, name={}, size={}", trackingId, type, name, size);
            progress.setId(trackingId);
            progress.setMediaId(savedMedia.getId());
            progress.setStartTime(timestamp);
            progress.setStatus(ProgressStatus.REQUESTED.getCode());
            Progress savedProgress = progressRepository.save(progress);

            ProgressDto progressDto = MapperConstant.PROGRESS.map(savedProgress);
            applicationEventPublisher.publishEvent(
                    new UploadEvent(this, type, savedMedia.getId(), trackingId, multipartFile));
            return progressDto;
        } catch(Exception e) {
            throw new MediaException("Unable to store " + type.toString() + ". ERROR=" + e.getMessage());
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

    private MediaContent getMediaContent(Type type, long id) {
        Optional<? extends MediaContent> optionalMediaContent = null;
        if (type == Type.IMAGE) {
            optionalMediaContent = imageRepository.findById(id);
        } else if (type == Type.VIDEO) {
            optionalMediaContent = videoRepository.findById(id);
        }
        return optionalMediaContent.orElseThrow(() -> new MediaException("Invalid Media ID"));
    }

}
