package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.ActivityProgressDto;
import com.sednar.digital.media.resource.v1.model.UploadProgressDto;
import com.sednar.digital.media.service.ActivityProgressService;
import com.sednar.digital.media.service.UploadProgressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/progress")
@Api(tags = "Progress", value = "APIs related to progress")
public class ProgressResource {

    private final ActivityProgressService activityProgressService;

    private final UploadProgressService uploadProgressService;

    @Autowired
    ProgressResource(ActivityProgressService activityProgressService,
                     UploadProgressService uploadProgressService) {
        this.activityProgressService = activityProgressService;
        this.uploadProgressService = uploadProgressService;
    }

    @GetMapping(path = "/upload/{type}")
    public List<UploadProgressDto> getUploadProgresses(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return uploadProgressService.getAll(type);
    }

    @GetMapping(path = "/upload/id/{id}")
    public UploadProgressDto getUploadProgress(@PathVariable String id) {
        return uploadProgressService.getProgress(id);
    }

    @GetMapping(path = "/activity/id/{id}")
    public ActivityProgressDto getActivityProgress(@PathVariable String id) {
        return activityProgressService.getProgress(id);
    }

}
