package com.sednar.digital.media.service.constants;

import com.sednar.digital.media.service.mapper.MediaMapper;
import com.sednar.digital.media.service.mapper.ProgressMapper;
import lombok.experimental.UtilityClass;
import org.mapstruct.factory.Mappers;

@UtilityClass
public class MapperConstant {

    public static final MediaMapper MEDIA = Mappers.getMapper(MediaMapper.class);

    public static final ProgressMapper PROGRESS = Mappers.getMapper(ProgressMapper.class);

}
