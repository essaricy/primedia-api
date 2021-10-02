package com.sednar.digital.media.common.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MediaExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "Exception Message";
        MediaException mediaException = new MediaException(message);
        Assertions.assertEquals(message, mediaException.getMessage());
    }

}