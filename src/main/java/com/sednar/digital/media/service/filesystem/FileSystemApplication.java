package com.sednar.digital.media.service.filesystem;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.service.config.properties.FileSystemProps;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class FileSystemApplication {

    private static final String STORAGE_KEY = "STORE";

    private static final String DOWNLOAD_KEY = "DOWNLOAD";

    private static final String WORK_KEY = "WORK";

    private static final String THUMB_KEY = "_THUMB";

    private final FileSystemProps fileSystemProps;

    private static final Map<String, File> SUBSYSTEMS_MAP = new HashMap<>();

    @Autowired
    public FileSystemApplication(FileSystemProps fileSystemProps) {
        this.fileSystemProps = fileSystemProps;
    }

    @PostConstruct
    private void init() throws IOException {
        initiateFileSystem();
    }

    private void initiateFileSystem() throws IOException {
        File baseDir = fileSystemProps.getBaseDir();
        FileUtils.forceMkdir(baseDir);
        createDirectory(baseDir, fileSystemProps.getBinDirName());

        File storageDir = createDirectory(baseDir, fileSystemProps.getStorageDirName());
        createSubSystem(storageDir, STORAGE_KEY, true);

        File workDirectory = createDirectory(baseDir, fileSystemProps.getWorkDirName());
        SUBSYSTEMS_MAP.put(WORK_KEY, workDirectory);

        File downloadDir = createDirectory(baseDir, fileSystemProps.getDownloadDirName());
        createSubSystem(downloadDir, DOWNLOAD_KEY, false);

        createDirectory(baseDir, fileSystemProps.getLogsDirName());
    }

    private void createSubSystem(File baseDir, String key, boolean createThumbs) {
        SUBSYSTEMS_MAP.put(key, baseDir);
        Arrays.stream(Type.values()).forEach(type -> {
            try {
                String typeName = type.name();
                File mediaDir = createDirectory(baseDir, typeName);
                SUBSYSTEMS_MAP.put(key + typeName, mediaDir);
                if (createThumbs) {
                    File thumbDirectory = createDirectory(mediaDir, fileSystemProps.getThumbnailDirName());
                    SUBSYSTEMS_MAP.put(key + typeName + THUMB_KEY, thumbDirectory);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private File createDirectory(File baseDir, String dirName) throws IOException {
        File directory = new File(baseDir, dirName.toLowerCase());
        FileUtils.forceMkdir(directory);
        return directory;
    }

    public File getWorkDir() {
        return SUBSYSTEMS_MAP.get(WORK_KEY);
    }

    public File getStorageDir(Type type) {
        return SUBSYSTEMS_MAP.get(STORAGE_KEY + type.name());
    }

    public File getDownloadDir(Type type) {
        return SUBSYSTEMS_MAP.get(DOWNLOAD_KEY + type.name());
    }

    public File getThumbnailDir(Type type) {
        return SUBSYSTEMS_MAP.get(STORAGE_KEY + type.name() + THUMB_KEY);
    }

}
