package com.sednar.digital.media.resource.v2;

import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.service.ContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/content/v2")
@Api(tags = "Content", value = "APIs related to Media content")
public class ContentResourceV2 {

    private static final String VIDEO_MP4 = "video/mp4";

    private final ContentService service;

    @Autowired
    ContentResourceV2(ContentService service) {
        this.service = service;
    }

    @GetMapping(path = "/{type}/{id}/thumb",
            produces={ MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getThumbnail(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) throws IOException {
        File file = service.getThumbFile(type, id);
        return FileUtils.readFileToByteArray(file);
    }

    @GetMapping(path = "/image/{id}", produces={ MediaType.IMAGE_JPEG_VALUE })
    public ResponseEntity<byte[]> getImage(@PathVariable long id) throws IOException {
        File file = service.getContentFile(Type.IMAGE, id);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), HttpStatus.OK);
    }

    @GetMapping(path = "/video/{id}", produces={ VIDEO_MP4 })
    public ResponseEntity<byte[]> getVideo(@PathVariable long id) throws IOException {
        File file = service.getContentFile(Type.VIDEO, id);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), HttpStatus.OK);
    }
}
