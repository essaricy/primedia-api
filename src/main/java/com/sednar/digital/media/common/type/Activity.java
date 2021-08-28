package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum Activity {

    GENERATE_THUMBS("GEN_THUMBS"),
    GENERATE_DURATIONS("GEN_DURATIONS"),
    SYNC_UP("SYNC_UP"),
    SYNC_DOWN("SYNC_DOWN"),
    REPLICATE("REPLICATE");

    @Getter
    private String code;

    Activity(String code) {
        this.code = code;
    }

    @JsonValue
    public String toCode() {
        return this.getCode();
    }

    @JsonCreator
    public static Activity fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.getCode(), code))
                .findAny().orElse(null);
    }

}