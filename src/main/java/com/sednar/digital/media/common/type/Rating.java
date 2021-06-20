package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Rating {

    POOR(1, "Poor"),
    NOT_BAD(2, "Not Bad"),
    OK(3, "Ok"),
    GOOD(4, "Good"),
    EXCELLENT(5, "Excellent");

    @Getter
    private int code;

    @Getter
    private String description;

    Rating(int code, String description) {
        this.code = code;
    }

    @JsonCreator
    public static Rating fromCode(int code) {
        return Arrays.stream(values()).filter(m -> m.getCode() == code).findAny().orElse(null);
    }

}
