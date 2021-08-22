package com.sednar.digital.media.resource.v1.model;

import com.sednar.digital.media.common.type.Quality;
import com.sednar.digital.media.common.type.Rating;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class MediaRequestDto {

    private String name;

    private Rating rating;

    private Quality quality;

    private Set<String> tags;

    private long size;

    private boolean addView;

    private boolean addLike;

}
