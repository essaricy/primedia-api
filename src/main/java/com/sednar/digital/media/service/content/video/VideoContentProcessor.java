package com.sednar.digital.media.service.content.video;

import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.MediaContent;
import com.sednar.digital.media.repo.entity.Video;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class VideoContentProcessor {

    private final VideoRepository videoRepository;

    @Autowired
    private FFmpegVideoService fFmpegVideoService;

    @Autowired
    VideoContentProcessor(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public double getVideoLength(File file) {
        String videoLength = fFmpegVideoService.getVideoLength(file);
        return StringUtils.isBlank(videoLength) ? 0 : Double.parseDouble(videoLength);
    }

    public File generateThumbnail(File file, double length) {
        return fFmpegVideoService.generateThumbnail(file, length);
    }

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
