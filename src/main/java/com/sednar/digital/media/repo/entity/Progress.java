package com.sednar.digital.media.repo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Progress {

    @Id
    private String id;

    private Long mediaId;

    private String status;

    private Timestamp startTime;

    private Timestamp endTime;

}
