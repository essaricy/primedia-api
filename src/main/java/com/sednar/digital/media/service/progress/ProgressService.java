package com.sednar.digital.media.service.progress;

import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.Progress;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class ProgressService {

    private final ProgressRepository progressRepository;

    private final MediaRepository mediaRepository;

    @Autowired
    ProgressService(ProgressRepository progressRepository,
                    MediaRepository mediaRepository) {
        this.progressRepository = progressRepository;
        this.mediaRepository = mediaRepository;
    }

    public List<ProgressDto> getAll(Type type) {
        List<Progress> progresses = progressRepository.findAllByOrderByStartTimeDesc();
        List<Media> mediaList = mediaRepository.findByType(type.getCode());
        List<ProgressDto> progressList = MapperConstant.PROGRESS.map(progresses);
        progressList.forEach(progress -> progress.setMedia(
                MapperConstant.MEDIA.map(
                mediaList.stream()
                .filter(media -> media.getId() == progress.getMediaId())
                .findFirst()
                .orElse(null))));
        return progressList;
    }

    public ProgressDto getProgress(String id) {
        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Progress not found for the id: " + id));
        return MapperConstant.PROGRESS.map(progress);
    }

    public ProgressDto save(String trackingId, long mediaId) {
        Progress progress = new Progress();
        progress.setId(trackingId);
        progress.setMediaId(mediaId);
        progress.setStartTime(new Timestamp(System.currentTimeMillis()));
        progress.setStatus(ProgressStatus.REQUESTED.getCode());
        Progress savedProgress = progressRepository.save(progress);
        return MapperConstant.PROGRESS.map(savedProgress);
    }

}
