package com.sednar.digital.media.service.sync;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.filesystem.FileSystemClient;
import com.sednar.digital.media.repo.SyncProgressRepository;
import com.sednar.digital.media.repo.VideoRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.SyncProgress;
import com.sednar.digital.media.repo.entity.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class SyncDownVideoService {

    private final FileSystemClient fileSystemClient;

    private final SyncProgressRepository syncProgressRepository;

    private final VideoRepository videoRepository;

    @Autowired
    public SyncDownVideoService(FileSystemClient fileSystemClient,
                                SyncProgressRepository syncProgressRepository,
                                VideoRepository videoRepository) {
        this.fileSystemClient = fileSystemClient;
        this.syncProgressRepository = syncProgressRepository;
        this.videoRepository = videoRepository;
    }

    public void sync(List<Media> mediaList, SyncProgress syncProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (Media media : mediaList) {
            Long mediaId = media.getId();
            String fileName = String.valueOf(mediaId);
            try {
                if (fileSystemClient.exists(Type.VIDEO, fileName)) {
                    syncProgressRepository.updateOnSkipped(++skipped, syncProgress);
                } else {
                    Video video = videoRepository.findById(mediaId)
                            .orElseThrow(() -> new ValidationException("No video found for id " + mediaId));
                    fileSystemClient.store(Type.IMAGE, fileName, video.getContent(), video.getThumbnail());
                    syncProgress.setSuccess(++success);
                    syncProgressRepository.save(syncProgress);
                    log.info("SyncDown successful for the media: : {}", mediaId);
                }
            } catch (Exception e) {
                syncProgressRepository.updateOnException(++failed, syncProgress);
            }
        }
        syncProgress.setEndTime(new Timestamp(System.currentTimeMillis()));
        syncProgressRepository.save(syncProgress);
    }

}
