package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum UploadStatus {

    INIT,
    THUMB_DONE,
    THUMB_FAIL,
    DB_DONE,
    DB_FAIL,
    FILE_DONE,
    FILE_FAIL,
    ALL_DONE;

    @JsonCreator
    public static UploadStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.toString(), code))
                .findAny().orElse(null);
    }

    @JsonValue
    public static String toCode(UploadStatus uploadStatus) {
        return uploadStatus.getCode();
    }

    public String getCode() {
        return this.toString();
    }
}
