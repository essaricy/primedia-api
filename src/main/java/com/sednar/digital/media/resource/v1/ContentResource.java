package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.BatchDto;
import com.sednar.digital.media.service.ContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/content")
@Api(tags = "Content", value = "APIs related to Media content")
public class ContentResource {

    private final ContentService service;

    @Autowired
    ContentResource(ContentService service) {
        this.service = service;
    }

    @GetMapping(path = "/{type}/{id}",
            produces={ MediaType.IMAGE_JPEG_VALUE, "video/mp4" })
    public byte[] getContent(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) {
        return service.getContent(type, id);
    }

    @GetMapping(path = "/{type}/{id}/thumb",
            produces={ MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getThumbnail(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) {
        return service.getThumbnail(type, id);
    }

    @PatchMapping(path = "/{type}/all")
    public BatchDto generateThumbs(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type) {
        return service.generateThumbs(type);
    }

}
