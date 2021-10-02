package com.sednar.digital.media.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DurationUtilTest {

    @Test
    public void test_getDurationStamp() {
        String durationStamp = DurationUtil.getDurationStamp(128);
        Assertions.assertEquals("02:08", durationStamp);
    }
}