package com.sednar.digital.media.repo;

import com.sednar.digital.media.common.type.UploadStatus;
import com.sednar.digital.media.repo.entity.UploadProgress;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UploadProgressRepository extends CrudRepository<UploadProgress, String> {

    String CACHE_NAME = "UploadProgressCache";

    @CachePut(cacheNames=CACHE_NAME, key="#trackingId")
    default UploadProgress start(String trackingId, long mediaId) {
        UploadProgress uploadProgress = new UploadProgress();
        uploadProgress.setId(trackingId);
        uploadProgress.setMediaId(mediaId);
        uploadProgress.setStartTime(new Timestamp(System.currentTimeMillis()));
        uploadProgress.setStatus(UploadStatus.INIT.getCode());
        return save(uploadProgress);
    }

    @CachePut(cacheNames=CACHE_NAME, key="#uploadProgress.id")
    default UploadProgress updateOnStep(UploadProgress uploadProgress, UploadStatus status) {
        uploadProgress.setStatus(status.getCode());
        return save(uploadProgress);
    }

    @CachePut(cacheNames=CACHE_NAME, key="#uploadProgress.id")
    default UploadProgress updateOnException(UploadProgress uploadProgress, UploadStatus status, Exception e) {
        uploadProgress.setStatus(status.getCode());
        uploadProgress.setErrorMessage(e.getMessage());
        return save(uploadProgress);
    }

    @CachePut(cacheNames=CACHE_NAME, key="#uploadProgress.id")
    default UploadProgress end(UploadProgress uploadProgress) {
        uploadProgress.setEndTime(new Timestamp(System.currentTimeMillis()));
        return save(uploadProgress);
    }

    @Cacheable(cacheNames=CACHE_NAME)
    List<UploadProgress> findAll();

    @Cacheable(cacheNames=CACHE_NAME)
    Optional<UploadProgress> findById(String id);

    @Cacheable(cacheNames=CACHE_NAME)
    List<UploadProgress> findAllByOrderByStartTimeDesc();

    @CachePut(cacheNames=CACHE_NAME, key="#uploadProgress.id")
    UploadProgress save(UploadProgress uploadProgress);

}
