package com.sednar.digital.media.filesystem;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@ConfigurationProperties("app.file-system")
@Getter
@Setter
public class FileSystemProps {

    private File baseDir;

    private String binDirName;

    private String storageDirName;

    private String thumbnailDirName;

    private String workDirName;

    private String logsDirName;

}
