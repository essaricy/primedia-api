package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.Image;
import com.sednar.digital.media.repo.selectors.ContentColumn;
import com.sednar.digital.media.repo.selectors.ThumbnailColumn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    List<Image> findAll();

    ThumbnailColumn getById(Long id);

    ContentColumn readById(Long id);

}
