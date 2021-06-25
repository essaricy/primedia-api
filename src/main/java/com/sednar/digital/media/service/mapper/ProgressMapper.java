package com.sednar.digital.media.service.mapper;

import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.repo.entity.Progress;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ProgressMapper {

    List<ProgressDto> map(List<Progress> list);

    ProgressDto map(Progress progress);

    default ProgressStatus mapStatus(String status) {
        return ProgressStatus.fromCode(status);
    }

}
