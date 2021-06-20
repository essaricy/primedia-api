package com.sednar.digital.media.common.file;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@UtilityClass
@Slf4j
public class FileSystem {

    private static final File APP_DIR = new File(FileUtils.getUserDirectory(), ".media");

    static {
        if (!APP_DIR.exists()) {
            APP_DIR.mkdir();
        }
    }

    public static File save(String fileName, byte[] bytes) throws IOException {
        File localFile = new File(APP_DIR, fileName);
        FileUtils.writeByteArrayToFile(localFile, bytes);
        log.info("File {} has been saved to {} ", fileName, localFile.getAbsolutePath());
        return localFile;
    }
}
