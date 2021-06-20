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
    private String description;

    Type(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static Type fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.getCode(), code))
                .findAny().orElse(null);
    }

}
