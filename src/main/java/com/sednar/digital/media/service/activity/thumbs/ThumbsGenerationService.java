package com.sednar.digital.media.service.activity.thumbs;

import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.repo.entity.Media;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ThumbsGenerationService {

    private final ImageThumbsGenerationService imageThumbsGenerationService;

    private final VideoThumbsGenerationService videoThumbsGenerationService;

    @Autowired
    public ThumbsGenerationService(
            ImageThumbsGenerationService imageThumbsGenerationService,
            VideoThumbsGenerationService videoThumbsGenerationService) {
        this.imageThumbsGenerationService = imageThumbsGenerationService;
        this.videoThumbsGenerationService = videoThumbsGenerationService;
    }

    @Async
    public void generateThumbs(Type type, List<Media> mediaList,
                                           ActivityProgress activityProgress,
                                           GenerationStrategy strategy) {
        if (type == Type.IMAGE) {
            imageThumbsGenerationService.generateThumbs(mediaList, activityProgress, strategy);
        } else if (type == Type.VIDEO) {
            videoThumbsGenerationService.generateThumbs(mediaList, activityProgress, strategy);
        }
    }

}
