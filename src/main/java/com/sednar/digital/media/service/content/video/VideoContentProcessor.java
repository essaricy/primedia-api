package com.sednar.digital.media.service.content.video;

import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.service.config.properties.VideoContentProcessingProps;
import com.sednar.digital.media.service.content.MediaContentProcessor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class VideoContentProcessor implements MediaContentProcessor<Video> {

    private final VideoContentProcessingProps properties;

    private final VideoRepository videoRepository;

    @Autowired
    private FFmpegVideoService fFmpegVideoService;

    @Autowired
    VideoContentProcessor(VideoContentProcessingProps properties,
                          VideoRepository videoRepository) {
        this.properties = properties;
        this.videoRepository = videoRepository;
    }

    @Override
    public Video process(Long mediaId, String trackingId, File uploadedFile) throws IOException {
        Video video = new Video();
        video.setId(mediaId);
        video.setContent(FileUtils.readFileToByteArray(uploadedFile));
        File thumbnailFile = fFmpegVideoService.generateThumbnail(uploadedFile);
        if (thumbnailFile != null) {
            video.setThumbnail(FileUtils.readFileToByteArray(thumbnailFile));
        }
        return videoRepository.save(video);
    }

}
