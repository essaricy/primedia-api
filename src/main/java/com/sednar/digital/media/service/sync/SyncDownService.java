package com.sednar.digital.media.service.sync;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.SyncProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SyncDownService {

    private final SyncDownImageService syncDownImageService;

    private final SyncDownVideoService syncDownVideoService;

    @Autowired
    public SyncDownService(SyncDownImageService syncDownImageService,
                           SyncDownVideoService syncDownVideoService) {
        this.syncDownImageService = syncDownImageService;
        this.syncDownVideoService = syncDownVideoService;
    }

    @Async
    public void sync(Type type, List<Media> mediaList, SyncProgress syncProgress) {
        if (type == Type.IMAGE) {
            syncDownImageService.sync(mediaList, syncProgress);
        } else if (type == Type.VIDEO) {
            syncDownVideoService.sync(mediaList, syncProgress);
        }
    }

}
