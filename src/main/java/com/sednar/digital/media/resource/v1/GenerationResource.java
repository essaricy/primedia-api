package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.GenerationStrategy;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.BatchDto;
import com.sednar.digital.media.service.GenerationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Deprecated
@RestController
@RequestMapping("/generate")
@Api(tags = "Generation", value = "APIs related to generation of media attributes")
public class GenerationResource {

    private final GenerationService service;

    @Autowired
    GenerationResource(GenerationService service) {
        this.service = service;
    }

    @PatchMapping(path = "/{type}/thumbs")
    public BatchDto generateThumbs(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(required = false, name = "strategy") GenerationStrategy strategy) {
        return service.generateThumbs(type, strategy);
    }

    @PatchMapping(path = "/{type}/durations")
    public BatchDto generateDurations(
            @RequestParam(required = false, name = "strategy") GenerationStrategy strategy) {
        return service.generateDurations(strategy);
    }

}
