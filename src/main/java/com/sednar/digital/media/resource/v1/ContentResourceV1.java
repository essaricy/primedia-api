package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.service.ContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/content/v1")
@Api(tags = "Content", value = "APIs related to Media content")
public class ContentResourceV1 {

    public static final String VIDEO_MP4 = "video/mp4";

    private final ContentService service;

    @Autowired
    ContentResourceV1(ContentService service) {
        this.service = service;
    }

    @GetMapping(path = "/{type}/{id}/thumb",
            produces={ MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getThumbnail(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) {
        return service.getThumbnail(type, id);
    }

    @GetMapping(path = "/image/{id}", produces={ MediaType.IMAGE_JPEG_VALUE })
    public ResponseEntity<byte[]> getImage(@PathVariable long id) {
        byte[] content = service.getContent(Type.IMAGE, id);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @GetMapping(path = "/video/{id}", produces={ VIDEO_MP4 })
    public ResponseEntity<byte[]> getVideo(@PathVariable long id) {
        byte[] content = service.getContent(Type.VIDEO, id);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

}
