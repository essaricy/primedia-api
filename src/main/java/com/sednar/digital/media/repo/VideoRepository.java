package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.Video;
import com.sednar.digital.media.repo.selectors.ContentColumn;
import com.sednar.digital.media.repo.selectors.ThumbnailColumn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long> {

    List<Video> findAll();

    ThumbnailColumn getById(Long id);

    ContentColumn readById(Long id);

}
