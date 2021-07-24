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

    List<Media> findTop5ByOrderByViewsAsc();

    List<Media> findTop5ByOrderByLikesAsc();

    List<Media> findTop5ByOrderByRatingDesc();

}
