package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.model.ActivityProgressDto;
import com.sednar.digital.media.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/activity")
@Api(tags = "Generation", value = "APIs related to generation of media attributes")
public class ActivityResource {

    private final ActivityService service;

    @Autowired
    ActivityResource(ActivityService service) {
        this.service = service;
    }

    @PatchMapping(path = "/{type}/generate/thumbs")
    public ActivityProgressDto generateThumbs(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(required = false, name = "strategy") GenerationStrategy strategy) {
        return service.generateThumbs(type, strategy);
    }

    @PatchMapping(path = "/{type}/generate/durations")
    public ActivityProgressDto generateDurations(
            @RequestParam(required = false, name = "strategy") GenerationStrategy strategy) {
        return service.generateDurations(strategy);
    }

    @PatchMapping(path = "/{type}/sync/up")
    public ActivityProgressDto syncUp(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.syncUp(type);
    }

    @PatchMapping(path = "/{type}/sync/down")
    public ActivityProgressDto syncDown(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.syncDown(type);
    }

    @PatchMapping(path = "/{type}/replicate")
    public ActivityProgressDto replicate(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.replicate(type);
    }

}
