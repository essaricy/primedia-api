package com.sednar.digital.media.repo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Getter
@Setter
@Entity
public class Video extends MediaContent {

    @Id
    protected Long id;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    protected byte[] content;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    protected byte[] thumbnail;

}
