package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.repo.entity.SyncProgress;
import com.sednar.digital.media.service.sync.SyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/sync")
@Api(tags = "Sync", value = "APIs related to synchronization of file system and database")
public class SyncResource {

    private final SyncService service;

    @Autowired
    SyncResource(SyncService service) {
        this.service = service;
    }

    @PatchMapping(path = "/{type}/up")
    public SyncProgress syncUp(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.syncUp(type);
    }

    @PatchMapping(path = "/{type}/down")
    public SyncProgress syncDown(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.syncDown(type);
    }

}
