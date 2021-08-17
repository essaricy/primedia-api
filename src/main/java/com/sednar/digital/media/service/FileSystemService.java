package com.sednar.digital.media.service;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.filesystem.FileSystemClient;
import com.sednar.digital.media.repo.ImageRepository;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Video;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;

@Service
public class FileSystemService {

    private final MediaRepository mediaRepository;

    private final ImageRepository imageRepository;

    private final VideoRepository videoRepository;

    private final FileSystemClient fileSystemClient;

    @Autowired
    public FileSystemService(MediaRepository mediaRepository,
                             ImageRepository imageRepository,
                             VideoRepository videoRepository,
                             FileSystemClient fileSystemClient) {
        this.mediaRepository = mediaRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.fileSystemClient = fileSystemClient;
    }

    public void sync(Type type) {
        List<Media> mediaList = mediaRepository.findByType(type.getCode());
        mediaList.stream().forEach(media -> {
            Long mediaId = media.getId();
            String fileName = String.valueOf(mediaId);
            Pair<byte[], byte[]> pair = null;
            try {
                if (!fileSystemClient.exists(type, fileName)) {
                    if (type == Type.IMAGE) {
                        pair = getImages(mediaId);
                    } else if (type == Type.VIDEO) {
                        pair = getVideos(mediaId);
                    }
                    fileSystemClient.store(Type.IMAGE, fileName, pair.getLeft(), pair.getRight());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Pair<byte[], byte[]> getImages(Long mediaId) {
        Image image = imageRepository.findById(mediaId)
                .orElseThrow(() -> new ValidationException("No image found for id " + mediaId));
        return Pair.of(image.getContent(), image.getThumbnail());
    }

    private Pair<byte[], byte[]> getVideos(Long mediaId) {
        Video video = videoRepository.findById(mediaId)
                .orElseThrow(() -> new ValidationException("No video found for id " + mediaId));
        return Pair.of(video.getContent(), video.getThumbnail());
    }

}
