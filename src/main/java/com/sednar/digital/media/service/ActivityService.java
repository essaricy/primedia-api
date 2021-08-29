package com.sednar.digital.media.service;

import com.sednar.digital.media.common.type.Activity;
import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.resource.model.ActivityProgressDto;
import com.sednar.digital.media.service.activity.duration.DurationsGenerationService;
import com.sednar.digital.media.service.activity.replicate.ReplicationService;
import com.sednar.digital.media.service.activity.sync.SyncDownService;
import com.sednar.digital.media.service.activity.sync.SyncUpService;
import com.sednar.digital.media.service.activity.thumbs.ThumbsGenerationService;
import com.sednar.digital.media.service.constants.MapperConstant;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class ActivityService {

    private final MediaRepository mediaRepository;

    private final ActivityProgressRepository activityProgressRepository;

    @Autowired
    private FileSystemClient fileSystemClient;

    @Autowired
    private ThumbsGenerationService thumbsGenerationService;

    @Autowired
    private DurationsGenerationService durationsGenerationService;

    @Autowired
    private SyncDownService syncDownService;

    @Autowired
    private SyncUpService syncUpService;

    @Autowired
    private ReplicationService replicationService;

    @Autowired
    public ActivityService(MediaRepository mediaRepository,
                           ActivityProgressRepository activityProgressRepository) {
        this.mediaRepository = mediaRepository;
        this.activityProgressRepository = activityProgressRepository;
    }

    public ActivityProgressDto generateThumbs(Type type, GenerationStrategy strategy) {
        List<Media> mediaList = mediaRepository.findByType(type.getCode());
        ActivityProgress activityProgress = activityProgressRepository.start(
                Activity.GENERATE_THUMBS, mediaList.size());
        thumbsGenerationService.generateThumbs(type, mediaList, activityProgress, strategy);
        return MapperConstant.ACTIVITY_PROGRESS.map(activityProgress);
    }

    public ActivityProgressDto generateDurations(GenerationStrategy strategy) {
        List<Media> mediaList = mediaRepository.findByType(Type.VIDEO.getCode());
        ActivityProgress activityProgress = activityProgressRepository.start(
                Activity.GENERATE_DURATIONS, mediaList.size());
        durationsGenerationService.generateDurations(mediaList, activityProgress, strategy);
        return MapperConstant.ACTIVITY_PROGRESS.map(activityProgress);
    }

    public ActivityProgressDto syncDown(Type type) {
        List<Media> mediaList = mediaRepository.findByType(type.getCode());
        ActivityProgress activityProgress = activityProgressRepository.start(
                Activity.SYNC_DOWN, mediaList.size());
        syncDownService.sync(type, mediaList, activityProgress);
        return MapperConstant.ACTIVITY_PROGRESS.map(activityProgress);
    }

    public ActivityProgressDto syncUp(Type type) {
        Collection<File> files = fileSystemClient.list(type);
        ActivityProgress activityProgress = activityProgressRepository.start(
                Activity.SYNC_DOWN, files.size());
        syncUpService.sync(type, files, activityProgress);
        return MapperConstant.ACTIVITY_PROGRESS.map(activityProgress);
    }

    public ActivityProgressDto replicate(Type type) {
        List<Media> mediaList = mediaRepository.findByType(type.getCode());
        ActivityProgress activityProgress = activityProgressRepository.start(
                Activity.REPLICATE, mediaList.size());
        replicationService.replicate(type, mediaList, activityProgress);
        return MapperConstant.ACTIVITY_PROGRESS.map(activityProgress);
    }
}
