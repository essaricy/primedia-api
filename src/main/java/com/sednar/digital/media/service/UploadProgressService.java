package com.sednar.digital.media.service;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.UploadProgress;
import com.sednar.digital.media.resource.model.UploadProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
public class UploadProgressService {

    private final UploadProgressRepository uploadProgressRepository;

    private final MediaRepository mediaRepository;

    @Autowired
    UploadProgressService(UploadProgressRepository uploadProgressRepository,
                          MediaRepository mediaRepository) {
        this.uploadProgressRepository = uploadProgressRepository;
        this.mediaRepository = mediaRepository;
    }

    public List<UploadProgressDto> getAll(Type type) {
        List<UploadProgress> uploadProgresses = uploadProgressRepository.findAllByOrderByStartTimeDesc();
        List<Media> mediaList = mediaRepository.findByType(type.getCode());
        List<UploadProgressDto> progressList = MapperConstant.UPLOAD_PROGRESS.map(uploadProgresses);
        progressList.forEach(progress -> progress.setMedia(
                MapperConstant.MEDIA.map(
                mediaList.stream()
                .filter(media -> media.getId() == progress.getMediaId())
                .findFirst()
                .orElse(null))));
        return progressList;
    }

    public UploadProgressDto getProgress(String id) {
        UploadProgress uploadProgress = uploadProgressRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Progress not found for the id: " + id));
        return MapperConstant.UPLOAD_PROGRESS.map(uploadProgress);
    }

}
