package com.sednar.digital.media.repo;

import com.sednar.digital.media.common.type.Activity;
import com.sednar.digital.media.repo.entity.ActivityProgress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public interface ActivityProgressRepository extends CrudRepository<ActivityProgress, String> {

    default ActivityProgress start(Activity activity, int total) {
        String trackingId = UUID.randomUUID().toString();
        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setActivity(activity.getCode());
        activityProgress.setId(trackingId);
        activityProgress.setTotal(total);
        activityProgress.setStartTime(new Timestamp(System.currentTimeMillis()));
        return save(activityProgress);
    }

    default void updateOnSuccess(int success, ActivityProgress activityProgress) {
        activityProgress.setSuccess(success);
        save(activityProgress);
    }

    default void updateOnSkipped(int skipped, ActivityProgress activityProgress) {
        activityProgress.setSkipped(skipped);
        save(activityProgress);
    }

    default void updateOnException(int failed, ActivityProgress activityProgress) {
        activityProgress.setFailed(failed);
        save(activityProgress);
    }

    default void end(ActivityProgress activityProgress) {
        activityProgress.setEndTime(new Timestamp(System.currentTimeMillis()));
        save(activityProgress);
    }

}
