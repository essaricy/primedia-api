package com.sednar.digital.media.resource.v1.model;

import com.sednar.digital.media.common.type.Activity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ActivityProgressDto {

    private String id;

    private Activity activity;

    private int total;

    private int success;

    private int failed;

    private int skipped;

    private Timestamp startTime;

    private Timestamp endTime;

}
