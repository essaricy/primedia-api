package com.sednar.digital.media.service.mapper;

import com.sednar.digital.media.common.type.UploadStatus;
import com.sednar.digital.media.repo.entity.UploadProgress;
import com.sednar.digital.media.resource.model.UploadProgressDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UploadProgressMapper {

    List<UploadProgressDto> map(List<UploadProgress> list);

    UploadProgressDto map(UploadProgress uploadProgress);

    default UploadStatus mapStatus(String status) {
        return UploadStatus.fromCode(status);
    }

}
