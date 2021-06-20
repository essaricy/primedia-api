package com.sednar.digital.media.repo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    private String type;

    private String name;

    private Integer quality;

    private Integer rating;

    private String tags;

    private long size;

    private int views;

    private int likes;

    private Timestamp uploadDate;

    private Timestamp lastSeen;

}
