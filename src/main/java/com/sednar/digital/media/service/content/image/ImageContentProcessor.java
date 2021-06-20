package com.sednar.digital.media.service.content.image;

import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.repo.entity.MediaContent;
import com.sednar.digital.media.service.content.MediaContentProcessor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ImageContentProcessor extends MediaContentProcessor {

    private final ImageRepository imageRepository;

    @Autowired
    ImageContentProcessor(ProgressRepository progressRepository,
                          ImageRepository imageRepository) {
        super(progressRepository);
        this.imageRepository = imageRepository;
    }

    @Override
    public File generateThumbnail(File file) throws IOException {
        return null;
    }

    @Override
    public MediaContent saveContent(Long mediaId, File content, File thumb) throws IOException {
        Image image = new Image();
        image.setId(mediaId);
        image.setContent(FileUtils.readFileToByteArray(content));
        if (thumb != null) {
            image.setThumbnail(FileUtils.readFileToByteArray(thumb));
        }
        return imageRepository.save(image);
    }

}
