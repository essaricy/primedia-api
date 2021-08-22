package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum Activity {

    GENERATE_THUMBS("GEN_THUMB"),
    GENERATE_DURATIONS("GEN_DUR"),
    SYNC_UP("SYNC_UP"),
    SYNC_DOWN("SYNC_DWN");

    @Getter
    private String code;

    Activity(String code) {
        this.code = code;
    }

    @JsonValue
    public static String toCode(Activity uploadStatus) {
        return uploadStatus.getCode();
    }

    @JsonCreator
    public static Activity fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.getCode(), code))
                .findAny().orElse(null);
    }

    public String getCode() {
        return this.toString();
    }
}
