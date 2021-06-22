package com.sednar.digital.media.resource.v1.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BatchDto {

    private long total;

    private long success;

    private long failed;

    private List<Long> successList;

    private List<Long> failureList;

}
