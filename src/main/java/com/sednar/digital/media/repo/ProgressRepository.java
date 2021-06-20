package com.sednar.digital.media.repo;

import com.sednar.digital.media.repo.entity.Progress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressRepository extends CrudRepository<Progress, Long> {

    List<Progress> findAll();

}
