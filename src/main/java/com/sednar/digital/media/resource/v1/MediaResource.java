
package com.sednar.digital.media.resource.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.MediaDto;
import com.sednar.digital.media.resource.v1.model.MediaRequest;
import com.sednar.digital.media.resource.v1.model.ProgressDto;
import com.sednar.digital.media.service.MediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/{type}")
@Api(tags = "Media", value = "APIs related to all types of Media")
public class MediaResource {

    private final MediaService service;

    @Autowired
    MediaResource(MediaService service) {
        this.service = service;
    }

    @GetMapping
    @ApiOperation(value = "Search images by text")
    public List<MediaDto> search(
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(name="s") String searchText) {
        return service.search(type, searchText);
    }

    @GetMapping(path = "/{id}/thumb",
            produces={ MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getThumbnail(
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) {
        return service.getThumbnail(type, id);
    }

    @GetMapping(path = "/{id}/content",
            produces={ MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE })
    public byte[] getContent(
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable long id) {
        return service.getContent(type, id);
    }

    @PostMapping
    @ApiOperation(value = "Upload any media type")
    public ProgressDto upload(
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam("request") String request,
            @RequestParam("file") MultipartFile file) throws IOException {
        MediaRequest mediaRequest = new ObjectMapper().readValue(request, MediaRequest.class);
        return service.upload(type, mediaRequest, file);
    }

    @PostMapping("/{id}")
    @ApiOperation(value = "Update media attributes")
    public MediaDto update(
            //@PathVariable @NotNull(message="Invalid Media Type") Type type,
            @PathVariable(name="id") long id,
            @RequestParam("request") MediaRequest mediaRequest) {
        return service.update(id, mediaRequest);
    }

}
