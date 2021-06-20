package com.sednar.digital.media.resource.v1.model;

import com.sednar.digital.media.common.type.ProgressStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProgressDto {

    private String id;

    private long mediaId;

    private ProgressStatus status;

}
