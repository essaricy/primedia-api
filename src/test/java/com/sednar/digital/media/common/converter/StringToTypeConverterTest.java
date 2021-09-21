package com.sednar.digital.media.common.converter;

import com.sednar.digital.media.common.type.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

//@ExtendWith(MockitoExtension.class)
public class StringToTypeConverterTest {

    @Test
    public void testAllValuesConverted() {
        StringToTypeConverter converter = new StringToTypeConverter();
        for (Type mock : Type.values()) {
            Type actual = converter.convert(mock.name());
            Assertions.assertEquals(mock, actual);
        }
    }
}