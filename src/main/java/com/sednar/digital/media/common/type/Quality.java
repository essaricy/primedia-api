package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

public enum Quality {

    LOW(1),
    MEDIUM(2),
    HIGH(3),
    HD(4);

    @Getter
    private final int code;

    Quality(int code) {
        this.code = code;
    }

    @JsonCreator
    public static Quality fromCode(int code) {
        return Arrays.stream(values()).filter(m -> m.getCode() == code).findAny().orElse(null);
    }

    @JsonValue
    public int toCode() {
        return getCode();
    }

}
