package com.sednar.digital.media.common.converter;

import com.sednar.digital.media.common.type.GenerationStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

//@ExtendWith(MockitoExtension.class)
public class StringToGenerationStrategyConverterTest {

    @Test
    public void testAllValuesConverted() {
        StringToGenerationStrategyConverter converter = new StringToGenerationStrategyConverter();
        for (GenerationStrategy mock : GenerationStrategy.values()) {
            GenerationStrategy actual = converter.convert(mock.name());
            Assertions.assertEquals(mock, actual);
        }
    }
}