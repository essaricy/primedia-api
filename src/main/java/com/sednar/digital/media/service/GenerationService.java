package com.sednar.digital.media.service;

import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.BatchDto;
import com.sednar.digital.media.service.generation.DurationsGenerationService;
import com.sednar.digital.media.service.generation.ThumbsGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GenerationService {

    private final ThumbsGenerationService thumbsGenerationService;

    private final DurationsGenerationService durationsGenerationService;

    @Autowired
    public GenerationService(ThumbsGenerationService thumbsGenerationService,
                             DurationsGenerationService durationsGenerationService) {
        this.thumbsGenerationService = thumbsGenerationService;
        this.durationsGenerationService = durationsGenerationService;
    }

    public BatchDto generateThumbs(Type type, GenerationStrategy strategy) {
        return thumbsGenerationService.generateThumbs(type, strategy);
    }

    public BatchDto generateDurations(GenerationStrategy strategy) {
        return durationsGenerationService.generateDurations(strategy);
    }

}
