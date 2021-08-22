package com.sednar.digital.media.repo;

import com.sednar.digital.media.common.type.UploadStatus;
import com.sednar.digital.media.repo.entity.UploadProgress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface UploadProgressRepository extends CrudRepository<UploadProgress, String> {

    List<UploadProgress> findAll();

    List<UploadProgress> findAllByOrderByStartTimeDesc();

    default UploadProgress initiate(String trackingId, long mediaId) {
        UploadProgress uploadProgress = new UploadProgress();
        uploadProgress.setId(trackingId);
        uploadProgress.setMediaId(mediaId);
        uploadProgress.setStartTime(new Timestamp(System.currentTimeMillis()));
        uploadProgress.setStatus(UploadStatus.INIT.getCode());
        return save(uploadProgress);
    }

}
