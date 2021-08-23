package com.sednar.digital.media.service;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.selectors.ContentColumn;
import com.sednar.digital.media.repo.selectors.ThumbnailColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContentService {

    private final ImageRepository imageRepository;

    private final VideoRepository videoRepository;

    @Autowired
    public ContentService(ImageRepository imageRepository,
                          VideoRepository videoRepository) {
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
    }

    public byte[] getThumbnail(Type type, long id) {
        // TODO: Read from the file system
        if (type == Type.VIDEO) {
            ThumbnailColumn column = videoRepository.getById(id);
            return column == null ? null : column.getThumbnail();
        } else if (type == Type.IMAGE) {
            return getContent(type, id);
        }
        return null;
    }

    public byte[] getContent(Type type, long id) {
        // TODO: Read from the file system
        ContentColumn column = null;
        if (type == Type.VIDEO) {
            column = videoRepository.readById(id);
        } else if (type == Type.IMAGE) {
            column = imageRepository.readById(id);
        }
        return column == null ? null : column.getContent();
    }

}
