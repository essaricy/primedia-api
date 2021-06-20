package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Type {

    IMAGE("I", "Image"),
    VIDEO("V", "Video");

    @Getter
    private String code;

    @Getter
    private String value;

    Type(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @JsonCreator
    public static Type fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.getCode(), code))
                .findAny().orElse(null);
    }

    public static Type fromValue(String value) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.getValue(), value))
                .findAny().orElse(null);
    }

}
