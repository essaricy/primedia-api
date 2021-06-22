package com.sednar.digital.media.service.content.image;

import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.repo.entity.MediaContent;
import com.sednar.digital.media.service.config.properties.ImageContentProcessingProps;
import com.sednar.digital.media.service.content.MediaContentProcessor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ImageContentProcessor extends MediaContentProcessor {

    private final ImageRepository imageRepository;

    private final ImageContentProcessingProps properties;

    @Autowired
    ImageContentProcessor(ProgressRepository progressRepository,
                          ImageRepository imageRepository,
                          ImageContentProcessingProps properties) {
        super(progressRepository);
        this.imageRepository = imageRepository;
        this.properties = properties;
    }

    @Override
    public File generateThumbnail(File file) throws IOException {
        File thumbnail = new File(file.getParent(), file.getName() + properties.getSuffix());
        Thumbnails.of(file)
                .width(properties.getWidth())
                .height(properties.getHeight())
                .toFile(thumbnail);
        return thumbnail;
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
