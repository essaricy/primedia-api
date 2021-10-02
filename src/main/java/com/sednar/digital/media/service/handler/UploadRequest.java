package com.sednar.digital.media.service.handler;

import com.sednar.digital.media.common.type.UploadStatus;
import com.sednar.digital.media.repo.entity.Media;
import com.sednar.digital.media.repo.entity.UploadProgress;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadRequest {

    public static final String MEDIA = "MEDIA";
    public static final String UPLOAD_PROGRESS = "UPLOAD_PROGRESS";
    public static final String WORKING_FILE = "WORKING_FILE";
    public static final String THUMB_FILE = "THUMB_FILE";
    public static final String UPLOAD_STATUS_SUCCESS = "UPLOAD_STATUS_SUCCESS";
    public static final String UPLOAD_STATUS_FAILURE = "UPLOAD_STATUS_FAILURE";
    public static final String LENGTH = "LENGTH";

    @Getter
    private final String trackingId;

    @Getter
    private final Long mediaId;

    private final Map<String, Object> attributes = new HashMap<>();

    public UploadRequest(@NotBlank String trackingId, @NotNull Long mediaId) {
        this.trackingId = trackingId;
        this.mediaId = mediaId;
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Media getMedia() {
        return (Media) attributes.get(MEDIA);
    }

    public File getFile(String key) {
        return (File) attributes.get(key);
    }

    public UploadProgress getUploadProgress() {
        return (UploadProgress) attributes.get(UPLOAD_PROGRESS);
    }

    public UploadStatus getUploadStatus(String key) {
        return (UploadStatus) attributes.get(key);
    }

    public double getDouble(String key) {
        return (Double) attributes.get(key);
    }

}