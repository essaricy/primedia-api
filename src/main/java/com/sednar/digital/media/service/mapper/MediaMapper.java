package com.sednar.digital.media.service.mapper;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Quality;
import com.sednar.digital.media.common.type.Rating;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.model.MediaDto;
import com.sednar.digital.media.repo.entity.Media;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface MediaMapper {

    List<MediaDto> map(List<Media> list);

    MediaDto map(Media media);

    Media map(MediaDto mediaDto);

    default Type mapType(String code) {
        return Type.fromCode(code);
    }

    default Quality mapQuality(int quality) {
        return Quality.fromCode(quality);
    }

    default Rating mapRating(int rating) {
        return Rating.fromCode(rating);
    }

    default Set<String> mapTags(String tagValues) {
        return Arrays.stream(Optional.ofNullable(tagValues)
                        .orElse("")
                        .split(MediaConstants.TAG_SEPARATOR))
                .collect(Collectors.toSet());
    }

    default String mapTags(Set<String> tags) {
        return String.join(MediaConstants.TAG_SEPARATOR, tags);
    }

}
