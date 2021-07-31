package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.BatchDto;
import com.sednar.digital.media.service.UtilityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/utility")
@Api(tags = "Utility", value = "Utility APIs related to Media")
public class UtilityResource {

    private final UtilityService service;

    @Autowired
    UtilityResource(UtilityService service) {
        this.service = service;
    }

    @PatchMapping(path = "/{type}/generate-thumbs")
    public BatchDto generateThumbs(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.generateThumbs(type);
    }

    @PatchMapping(path = "/generate-duration")
    public BatchDto generateDuration() {
        return service.generateDuration();
    }

}
