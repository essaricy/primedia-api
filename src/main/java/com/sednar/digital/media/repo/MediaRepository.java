package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.Media;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends CrudRepository<Media, Long> {

    List<Media> findAll();

    List<Media> findByTypeOrderByViewsDescLikesDesc(String type);

    List<Media> findByType(String code);

    // Dashboard queries
    List<Media> findAllByTypeOrderByUploadDateDesc(String type);

    // TODO: FIX: where param not 0
    List<Media> findAllByTypeOrderByViewsDesc(String type);

    // TODO: FIX: where param not 0
    List<Media> findAllByTypeOrderByLikesDesc(String type);

    List<Media> findAllByTypeOrderByRatingDesc(String type);

}
