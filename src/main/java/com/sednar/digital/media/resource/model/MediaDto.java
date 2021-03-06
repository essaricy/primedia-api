package com.sednar.digital.media.resource.model;

import com.sednar.digital.media.common.type.Type;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Setter
@Getter
public class MediaDto {

    private long id;

    private String name;

    private Type type;

    private int quality;

    private long size;

    private int rating;

    private Set<String> tags;

    private int views;

    private int likes;

    private Timestamp uploadDate;

    private Timestamp lastSeen;

    private String duration;

}
