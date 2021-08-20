package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.SyncProgress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public interface SyncProgressRepository extends CrudRepository<SyncProgress, String> {

    default SyncProgress createSyncProgress(int total) {
        String trackingId = UUID.randomUUID().toString();
        SyncProgress progress = new SyncProgress();
        progress.setId(trackingId);
        progress.setTotal(total);
        progress.setStartTime(new Timestamp(System.currentTimeMillis()));
        return save(progress);
    }

    default void updateOnSuccess(int success, SyncProgress syncProgress) {
        syncProgress.setSkipped(success);
        save(syncProgress);
    }

    default void updateOnSkipped(int skipped, SyncProgress syncProgress) {
        syncProgress.setSkipped(skipped);
        save(syncProgress);
    }

    default void updateOnException(int failed, SyncProgress syncProgress) {
        syncProgress.setFailed(failed);
        save(syncProgress);
    }

}
