package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum ProgressStatus {

    INIT,
    THUMB_DONE,
    THUMB_FAIL,
    DB_DONE,
    DB_FAIL,
    FILE_DONE,
    FILE_FAIL,
    ALL_DONE;

    @JsonCreator
    public static ProgressStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.toString(), code))
                .findAny().orElse(null);
    }

    @JsonValue
    public static String toCode(ProgressStatus progressStatus) {
        return progressStatus.getCode();
    }

    public String getCode() {
        return this.toString();
    }
}
