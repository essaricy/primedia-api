package com.sednar.digital.media.common.util;

import com.sednar.digital.media.exception.MediaException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@UtilityClass
@Slf4j
public class ProcessUtil {

    public static List<String> execute(String command) {
        long startTime = System.currentTimeMillis();
        log.info("Executing process started at {}, command: {}", new Date(), command);
        log.info("Executing command: {}", command);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            process.waitFor();
            int exitValue = process.exitValue();
            if (exitValue != 0) {
                throw new MediaException("Unable to execute command. Exit value was " + exitValue
                        + ". Command: " + command);
            }
            log.info("Executing process ended at {}, command: {}", new Date(), command);
            long endTime = System.currentTimeMillis();
            Duration duration = Duration.ofMillis(endTime - startTime);
            log.info("Total time take for command: {} is {}", command, duration);
            return IOUtils.readLines(process.getInputStream());
        } catch (IOException | InterruptedException e) {
            throw new MediaException("Unable to execute command. Error: " + e.getMessage()
                    + ". Command: " + command);
        }
    }

}
