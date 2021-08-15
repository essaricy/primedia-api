package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProgressStatus {

    REQUESTED("REQUESTED", "Requested for processing content"),
    PROCESS_STARTED("PROC_START", "Started processing content"),
    THUMBNAIL_GENERATED("THUMB_DONE", "Generated Thumbnail"),
    THUMBNAIL_FAILED("THUMB_FAIL", "Generating Thumbnail failed"),
    SAVE_DONE("SAVE_DONE", "Saved all media content"),
    SAVE_FAIL("SAVE_FAIL", "Saving media content failed");

    @Getter
    private final String code;

    @Getter
    private final String description;

    ProgressStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static ProgressStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.getCode(), code))
                .findAny().orElse(null);
    }

}
