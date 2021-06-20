package com.sednar.digital.media.service.content.video;

import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.MediaContent;
import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.service.config.properties.VideoContentProcessingProps;
import com.sednar.digital.media.service.content.MediaContentProcessor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class VideoContentProcessor extends MediaContentProcessor {

    private final VideoContentProcessingProps properties;

    private final VideoRepository videoRepository;

    @Autowired
    private FFmpegVideoService fFmpegVideoService;

    @Autowired
    VideoContentProcessor(ProgressRepository progressRepository,
                          VideoContentProcessingProps properties,
                          VideoRepository videoRepository) {
        super(progressRepository);
        this.properties = properties;
        this.videoRepository = videoRepository;
    }

    @Override
    public File generateThumbnail(File file) throws Exception {
        return fFmpegVideoService.generateThumbnail(file);
    }

    @Override
    public MediaContent saveContent(Long mediaId, File content, File thumb) throws IOException {
        Video video = new Video();
        video.setId(mediaId);
        video.setContent(FileUtils.readFileToByteArray(content));
        if (thumb != null) {
            video.setThumbnail(FileUtils.readFileToByteArray(thumb));
        }
        return videoRepository.save(video);
    }

}
