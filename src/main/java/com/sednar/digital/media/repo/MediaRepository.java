package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.Media;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends CrudRepository<Media, Long> {

    // String CACHE_NAME = "MediaCache";

    //@Cacheable(cacheNames=CACHE_NAME)
    List<Media> findAll();

    //@Cacheable(cacheNames=CACHE_NAME)
    List<Media> findByTypeOrderByViewsDescLikesDesc(String type);

    //@Cacheable(cacheNames=CACHE_NAME)
    List<Media> findByType(String code);

    // Dashboard queries
    //@Cacheable(cacheNames=CACHE_NAME)
    List<Media> findAllByTypeOrderByUploadDateDesc(String type);

    //@Cacheable(cacheNames=CACHE_NAME)
    List<Media> findAllByTypeOrderByViewsDesc(String type);

    //@Cacheable(cacheNames=CACHE_NAME)
    List<Media> findAllByTypeOrderByLikesDesc(String type);

    //@Cacheable(cacheNames= CACHE_NAME)
    List<Media> findAllByTypeOrderByRatingDesc(String type);

    //@CachePut(cacheNames=CACHE_NAME, key="#media.id")
    Media save(Media media);

}
