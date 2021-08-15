package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.progress.ProgressService;
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

    private final ProgressService service;

    @Autowired
    ProgressResource(ProgressService service) {
        this.service = service;
    }

    @GetMapping(path = "/{type}")
    public List<ProgressDto> getAll(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.getAll(type);
    }

    @GetMapping(path = "/id/{id}")
    public ProgressDto getProgress(@PathVariable String id) {
        return service.getProgress(id);
    }

}
