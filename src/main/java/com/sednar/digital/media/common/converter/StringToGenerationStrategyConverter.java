package com.sednar.digital.media.common.converter;

import com.sednar.digital.media.common.type.GenerationStrategy;
import org.springframework.core.convert.converter.Converter;

public class StringToGenerationStrategyConverter implements Converter<String, GenerationStrategy> {

    @Override
    public GenerationStrategy convert(String value) {
        return GenerationStrategy.fromValue(value);
    }

}
