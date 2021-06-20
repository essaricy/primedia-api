package com.sednar.digital.media.service.content.image;

import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.service.content.MediaContentProcessor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ImageContentProcessor implements MediaContentProcessor<Image> {

    private final ImageRepository imageRepository;

    @Autowired
    ImageContentProcessor(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image process(Long mediaId, String trackingId, File file) throws IOException {
        Image image = new Image();
        image.setId(mediaId);
        image.setContent(FileUtils.readFileToByteArray(file));
        File thumbnailFile = null;
        if (thumbnailFile != null) {
            image.setThumbnail(FileUtils.readFileToByteArray(thumbnailFile));
        }
        return imageRepository.save(image);
    }

}
