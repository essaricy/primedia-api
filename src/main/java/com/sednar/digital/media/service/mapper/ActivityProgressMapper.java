package com.sednar.digital.media.service.mapper;

import com.sednar.digital.media.repo.entity.ActivityProgress;
import com.sednar.digital.media.resource.v1.model.ActivityProgressDto;
import org.mapstruct.Mapper;

@Mapper
public interface ActivityProgressMapper {

    ActivityProgressDto map(ActivityProgress activityProgress);

}
