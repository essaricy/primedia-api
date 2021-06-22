package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.repo.selectors.ContentColumn;
import com.sednar.digital.media.repo.selectors.ThumbnailColumn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long> {

    ThumbnailColumn getById(Long id);

    ContentColumn readById(Long id);

}
