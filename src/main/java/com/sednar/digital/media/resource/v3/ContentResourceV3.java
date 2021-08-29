package com.sednar.digital.media.resource.v3;

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
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@RestController
@RequestMapping("/content/v3")
@Api(tags = "Content", value = "APIs related to Media content")
public class ContentResourceV3 {

    private static final String VIDEO_MP4 = "video/mp4";
    private static final String ACCEPT_RANGES = "Accept-Ranges";
    private static final String BYTES = "bytes";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_RANGE = "Content-Range";
    private static final String CONTENT_TYPE = "Content-Type";

    private final ContentService service;

    @Autowired
    ContentResourceV3(ContentService service) {
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
    public ResponseEntity<byte[]> getVideo(@PathVariable long id,
                                           @RequestHeader(value = "Range", required = false) String range) {
        File file = service.getContentFile(Type.VIDEO, id);
        long fileSize = FileUtils.sizeOf(file);
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;

        if (range == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header(CONTENT_TYPE, VIDEO_MP4)
                    .header(CONTENT_LENGTH, String.valueOf(fileSize))
                    .body(readInRange(file, rangeStart, fileSize - 1)); // Read the object and convert it as bytes
        }
        String[] ranges = range.split("-");
        rangeStart = Long.parseLong(ranges[0].substring(6));
        if (ranges.length > 1) {
            rangeEnd = Long.parseLong(ranges[1]);
        } else {
            rangeEnd = fileSize - 1;
        }
        if (fileSize < rangeEnd) {
            rangeEnd = fileSize - 1;
        }
        data = readInRange(file, rangeStart, rangeEnd);

        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, VIDEO_MP4)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header("Range", range)
                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);
    }

    private byte[] readInRange(File file, long start, long end) {
        byte[] buffer = new byte[(int)(end - start) + 1];

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r"))
        {
            randomAccessFile.seek(start);
            randomAccessFile.readFully(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
