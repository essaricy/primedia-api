package com.sednar.digital.media.resource.v1.model;

import com.sednar.digital.media.common.type.UploadStatus;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class UploadProgressDto {

    private String id;

    private long mediaId;

    private UploadStatus status;

    private Timestamp startTime;

    private Timestamp endTime;

    private String errorMessage;

    private MediaDto media;

}
