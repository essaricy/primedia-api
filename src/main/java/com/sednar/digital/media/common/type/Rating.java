package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

public enum Rating {

    POOR(1),
    NOT_BAD(2),
    OK(3),
    GOOD(4),
    EXCELLENT(5);

    @Getter
    private final int code;

    Rating(int code) {
        this.code = code;
    }

    @JsonCreator
    public static Rating fromCode(int code) {
        return Arrays.stream(values()).filter(m -> m.getCode() == code).findAny().orElse(null);
    }

    @JsonValue
    public int toCode() {
        return getCode();
    }

}
