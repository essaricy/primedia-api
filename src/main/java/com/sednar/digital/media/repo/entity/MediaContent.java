package com.sednar.digital.media.repo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.Lob;

@Getter
@Setter
public abstract class MediaContent {

    @Id
    protected Long id;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    protected byte[] content;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    protected byte[] thumbnail;

}
