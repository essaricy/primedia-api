package com.sednar.digital.media.resource.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sednar.digital.media.common.constants.MediaConstants;
import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.resource.v1.model.MediaDto;
import com.sednar.digital.media.resource.v1.model.MediaRequestDto;
import com.sednar.digital.media.resource.v1.model.UploadProgressDto;
import com.sednar.digital.media.service.MediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/media")
@Api(tags = "Media", value = "APIs related to all types of Media")
public class MediaResource {

    private final MediaService service;

    @Autowired
    MediaResource(MediaService service) {
        this.service = service;
    }

    @GetMapping("/{type}")
    @ApiOperation(value = "Search images by text")
    public List<MediaDto> search(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(name="s") String searchText) {
        return service.search(type, searchText);
    }

    @PostMapping("/{type}")
    @ApiOperation(value = "Upload any media type")
    public UploadProgressDto upload(
            @ApiParam(defaultValue = "Video", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam("request") String request,
            @RequestParam("file") MultipartFile file) throws IOException {
        MediaRequestDto mediaRequestDto = new ObjectMapper().readValue(request, MediaRequestDto.class);
        return service.upload(type, mediaRequestDto, file);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update media attributes")
    public MediaDto update(
            @PathVariable(name="id") long id,
            @RequestBody MediaRequestDto mediaRequestDto) {
        return service.update(id, mediaRequestDto);
    }

    @GetMapping("/{type}/recent")
    @ApiOperation(value = "Get most recent media")
    public List<MediaDto> getMostRecent(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(name = "max", required = false) @Valid
            @Min(value=3, message = "Minimum value cannot be lesser than {value}") Integer max) {
        return service.getMostRecent(type, max);
    }

    @GetMapping("/{type}/viewed")
    @ApiOperation(value = "Get most viewed media")
    public List<MediaDto> getMostViewed(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(name = "max", required = false) @Valid
            @Min(value=3, message = "Minimum value cannot be lesser than {value}") Integer max) {
        return service.getMostViewed(type, max);
    }

    @GetMapping("/{type}/liked")
    @ApiOperation(value = "Get most liked media")
    public List<MediaDto> getMostLiked(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(name = "max", required = false) @Valid
            @Min(value=3, message = "Minimum value cannot be lesser than {value}") Integer max) {
        return service.getMostLiked(type, max);
    }

    @GetMapping("/{type}/rated")
    @ApiOperation(value = "Get most rated media")
    public List<MediaDto> getMostRated(
            @ApiParam(defaultValue = "Image", allowableValues = MediaConstants.TYPES)
            @PathVariable @NotNull(message="Invalid Media Type") Type type,
            @RequestParam(name = "max", required = false) @Valid
            @Min(value=3, message = "Minimum value cannot be lesser than {value}") Integer max) {
        return service.getMostRated(type, max);
    }

}
