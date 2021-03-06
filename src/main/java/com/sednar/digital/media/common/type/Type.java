package com.sednar.digital.media.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum Type {

    IMAGE("I"),
    VIDEO("V");

    @Getter
    private final String code;

    Type(String code) {
        this.code = code;
    }

    @JsonCreator
    public static Type fromCode(String code) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.getCode(), code))
                .findAny().orElse(null);
    }

    public static Type fromValue(String value) {
        return Arrays.stream(values())
                .filter(m -> StringUtils.equalsIgnoreCase(m.toString(), value))
                .findAny().orElse(null);
    }

    @JsonValue
    public String toCode() {
        return getCode();
    }

}
