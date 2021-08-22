package com.sednar.digital.media.service;

import com.sednar.digital.media.repo.ActivityProgressRepository;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.resource.v1.model.ActivityProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;

@Service
@Slf4j
public class ActivityProgressService {

    private final ActivityProgressRepository activityProgressRepository;

    @Autowired
    ActivityProgressService(
            ActivityProgressRepository activityProgressRepository) {
        this.activityProgressRepository = activityProgressRepository;
    }

    public ActivityProgressDto getProgress(String id) {
        ActivityProgress activityProgress = activityProgressRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Progress not found for the id: " + id));
        return MapperConstant.ACTIVITY_PROGRESS.map(activityProgress);
    }

}
