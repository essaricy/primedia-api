package com.sednar.digital.media.common.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DurationFormatUtils;

@UtilityClass
public class DurationUtil {

    public static String getDurationStamp(double seconds) {
        int millis = (int) seconds * 1000;
        return DurationFormatUtils.formatDuration(millis, "mm:ss");
    }

}
