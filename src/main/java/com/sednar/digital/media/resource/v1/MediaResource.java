package com.sednar.digital.media.resource.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.MediaDto;
import com.sednar.digital.media.resource.v1.model.MediaRequest;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.MediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/media")
@Api(tags = "Media", value = "APIs related to all types of Media")
public class MediaResource {

    public static final String TYPES = "Image,Video";

    private final MediaService service;

    @Autowired
    MediaResource(MediaService service) {
        this.service = service;
    }

    @GetMapping("/{type}")
    @ApiOperation(value = "Search images by text")
    public List<MediaDto> search(
            @ApiParam(defaultValue = "Video", allowableValues = TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(name="s") String searchText) {
        return service.search(type, searchText);
    }

    @GetMapping(path = "/{type}/{id}/thumb",
            produces={ MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getThumbnail(
            @ApiParam(defaultValue = "Video", allowableValues = TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) {
        return service.getThumbnail(type, id);
    }

    @GetMapping(path = "/{type}/{id}/content",
            produces={ MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getContent(
            @ApiParam(defaultValue = "Video", allowableValues = TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) {
        return service.getContent(type, id);
    }

    @PostMapping("/{type}")
    @ApiOperation(value = "Upload any media type")
    public ProgressDto upload(
            @ApiParam(defaultValue = "Video", allowableValues = TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam("request") String request,
            @RequestParam("file") MultipartFile file) throws IOException {
        MediaRequest mediaRequest = new ObjectMapper().readValue(request, MediaRequest.class);
        return service.upload(type, mediaRequest, file);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update media attributes")
    public MediaDto update(
            @PathVariable(name="id") long id,
            @RequestParam("request") MediaRequest mediaRequest) {
        return service.update(id, mediaRequest);
    }

}
