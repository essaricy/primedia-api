package com.sednar.digital.media.repo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class ActivityProgress {

    @Id
    private String id;

    private String activity;

    private int total;

    private int success;

    private int failed;

    private int skipped;

    private Timestamp startTime;

    private Timestamp endTime;

}
