package com.sednar.digital.media.service.sync;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.filesystem.FileSystemClient;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.SyncProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.SyncProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class SyncService {

    private final MediaRepository mediaRepository;

    private final FileSystemClient fileSystemClient;

    private final SyncProgressRepository syncProgressRepository;

    private SyncDownService syncDownService;

    private SyncUpService syncUpService;

    @Autowired
    public SyncService(MediaRepository mediaRepository,
                       FileSystemClient fileSystemClient,
                       SyncProgressRepository syncProgressRepository,
                       SyncDownService syncDownService,
                       SyncUpService syncUpService) {
        this.mediaRepository = mediaRepository;
        this.fileSystemClient = fileSystemClient;
        this.syncProgressRepository = syncProgressRepository;
        this.syncDownService = syncDownService;
        this.syncUpService = syncUpService;
    }

    public SyncProgress syncDown(Type type) {
        List<Media> mediaList = mediaRepository.findByType(type.getCode());
        SyncProgress syncProgress = syncProgressRepository.createSyncProgress(mediaList.size());
        syncDownService.sync(type, mediaList, syncProgress);
        return syncProgress;
    }

    public SyncProgress syncUp(Type type) {
        Collection<File> files = fileSystemClient.list(type);
        SyncProgress syncProgress = syncProgressRepository.createSyncProgress(files.size());
        syncUpService.sync(type, files, syncProgress);
        return syncProgress;
    }

}
