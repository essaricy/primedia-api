package com.sednar.digital.media.service.sync;

import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.SyncProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.SyncProgress;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.util.DurationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class SyncUpVideoService {

    private final MediaRepository mediaRepository;

    private final VideoContentProcessor videoContentProcessor;

    private final SyncProgressRepository syncProgressRepository;

    @Autowired
    public SyncUpVideoService(MediaRepository mediaRepository,
                              VideoContentProcessor videoContentProcessor,
                              SyncProgressRepository syncProgressRepository) {
        this.mediaRepository = mediaRepository;
        this.videoContentProcessor = videoContentProcessor;
        this.syncProgressRepository = syncProgressRepository;
    }

    @Async
    public void sync(Collection<File> files, SyncProgress syncProgress) {
        int success = 0;
        int skipped = 0;
        int failed = 0;

        for (File file : files) {
            Long id = Long.parseLong(file.getName());
            Optional<Media> mediaOptional = mediaRepository.findById(id);
            try {
                if (mediaOptional.isPresent()) {
                    Media media = mediaOptional.get();
                    Thread.sleep(2000);
                    double videoLength = videoContentProcessor.getVideoLength(file);
                    File thumb = videoContentProcessor.generateThumbnail(file, videoLength);
                    videoContentProcessor.saveContent(id, file, thumb);
                    media.setDuration(DurationUtil.getDurationStamp(videoLength));
                    mediaRepository.save(media);
                    syncProgressRepository.updateOnSuccess(++success, syncProgress);
                } else {
                    syncProgressRepository.updateOnSkipped(++skipped, syncProgress);
                }
            } catch (Exception e) {
                syncProgressRepository.updateOnException(++failed, syncProgress);
            }
        }
        syncProgress.setEndTime(new Timestamp(System.currentTimeMillis()));
        syncProgressRepository.save(syncProgress);
    }

}
