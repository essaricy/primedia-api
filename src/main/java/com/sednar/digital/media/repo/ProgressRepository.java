package com.sednar.digital.media.repo;

import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.repo.entity.Progress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ProgressRepository extends CrudRepository<Progress, String> {

    List<Progress> findAll();

    List<Progress> findAllByOrderByStartTimeDesc();

    default Progress initiateProgress(String trackingId, long mediaId) {
        Progress progress = new Progress();
        progress.setId(trackingId);
        progress.setMediaId(mediaId);
        progress.setStartTime(new Timestamp(System.currentTimeMillis()));
        progress.setStatus(ProgressStatus.INIT.getCode());
        Progress savedProgress = save(progress);
        return savedProgress;
    }

}
