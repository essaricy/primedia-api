package com.sednar.digital.media.service.sync;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.SyncProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.SyncProgress;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.util.DurationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SyncUpService {

    private final SyncUpImageService syncUpImageService;

    private final SyncUpVideoService syncUpVideoService;

    @Autowired
    public SyncUpService(SyncUpImageService syncUpImageService,
                           SyncUpVideoService syncUpVideoService) {
        this.syncUpImageService = syncUpImageService;
        this.syncUpVideoService = syncUpVideoService;
    }

    @Async
    public void sync(Type type, Collection<File> files, SyncProgress syncProgress) {
        if (type == Type.IMAGE) {
            syncUpImageService.sync(files, syncProgress);
        } else if (type == Type.VIDEO) {
            syncUpVideoService.sync(files, syncProgress);
        }
    }

}
