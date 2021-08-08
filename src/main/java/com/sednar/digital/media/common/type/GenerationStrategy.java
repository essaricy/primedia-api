package com.sednar.digital.media.common.type;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum GenerationStrategy {

    ALL,
    ONLY_ABSENT;

    public static GenerationStrategy fromValue(String value) {
        return Arrays.stream(values())
                .filter(gs -> StringUtils.equalsIgnoreCase(gs.toString(), value))
                .findFirst()
                .orElse(GenerationStrategy.ALL);
    }
}
