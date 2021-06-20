package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Quality {

    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High"),
    HD(4, "High Definition");

    @Getter
    private int code;

    @Getter
    private String description;

    Quality(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static Quality fromCode(int code) {
        return Arrays.stream(values()).filter(m -> m.getCode() == code).findAny().orElse(null);
    }


}
