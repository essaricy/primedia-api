package com.sednar.digital.media.service.constants;

import com.sednar.digital.media.service.mapper.ActivityProgressMapper;
import com.sednar.digital.media.service.mapper.MediaMapper;
import com.sednar.digital.media.service.mapper.UploadProgressMapper;
import lombok.experimental.UtilityClass;
import org.mapstruct.factory.Mappers;

@UtilityClass
public class MapperConstant {

    public static final MediaMapper MEDIA = Mappers.getMapper(MediaMapper.class);

    public static final UploadProgressMapper UPLOAD_PROGRESS = Mappers.getMapper(UploadProgressMapper.class);

    public static final ActivityProgressMapper ACTIVITY_PROGRESS = Mappers.getMapper(ActivityProgressMapper.class);

}
