package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.Media;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends CrudRepository<Media, Long> {

    List<Media> findAll();

    List<Media> findAllByOrderByViewsDescLikesDesc();

    List<Media> findByTypeOrderByViewsDescLikesDesc(String type);

    List<Media> findTop5ByOrderByUploadDateDesc();

    List<Media> findTop5ByOrderByViewsDesc();

    List<Media> findTop5ByOrderByLikesDesc();

    List<Media> findTop5ByOrderByRatingDesc();

    List<Media> findAllByOrderByUploadDateDesc();

    List<Media> findAllByOrderByViewsDesc();

    List<Media> findAllByOrderByLikesDesc();

    List<Media> findAllByOrderByRatingDesc();

    List<Media> findByType(String code);

}
