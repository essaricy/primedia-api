package com.sednar.digital.media.common.converter;

import com.sednar.digital.media.common.type.Type;
import org.springframework.core.convert.converter.Converter;

public class StringToTypeConverter implements Converter<String, Type> {

    @Override
    public Type convert(String value) {
        return Type.fromValue(value);
    }

}
