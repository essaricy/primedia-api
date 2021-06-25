package com.sednar.digital.media.service.progress;

import com.sednar.digital.media.common.type.ProgressStatus;
import com.sednar.digital.media.exception.MediaException;
import com.sednar.digital.media.repo.ProgressRepository;
import com.sednar.digital.media.repo.entity.Progress;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.constants.MapperConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class ProgressService {

    private final ProgressRepository progressRepository;

    @Autowired
    ProgressService(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<ProgressDto> getAll() {
        List<Progress> progresses = progressRepository.findByOrderByStartTimeDesc();
        Collections.sort(progresses, Comparator.comparing(Progress::getStartTime));
        return MapperConstant.PROGRESS.map(progresses);
    }

    public ProgressDto getProgress(String id) {
        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new MediaException("Progress not found for the id: " + id));
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
