package com.sednar.digital.media.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.MessageFormat;

@UtilityClass
public class FileUtil {

    public static void deleteQuietly(File... files) {
        for (File file : files) {
            FileUtils.deleteQuietly(file);
        }
    }

    public static String getImageFileName(String sessionId, Long id) {
        return MessageFormat.format("{0}_{1}.jpg", sessionId, id);
    }

    public static String getVideoFileName(String sessionId, Long id) {
        return MessageFormat.format("{0}_{1}", sessionId, id);
    }

}
