package com.sednar.digital.media.filesystem;

import com.sednar.digital.media.common.exception.MediaException;
import com.sednar.digital.media.common.type.Type;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class FileSystemClient {

    private final FileSystemApplication fileSystemApplication;

    public FileSystemClient(FileSystemApplication fileSystemApplication) {
        this.fileSystemApplication = fileSystemApplication;
    }

    public File createWorkingFile(String trackingId, byte[] data) throws IOException {
        File workDir = fileSystemApplication.getWorkDir();
        File savedFile = new File(workDir, trackingId);
        FileUtils.writeByteArrayToFile(savedFile, data);
        log.info("Created a working file with trackingId={}", trackingId);
        return savedFile;
    }

    public File getWorkingFile(String trackingId) throws IOException {
        File workDir = fileSystemApplication.getWorkDir();
        File savedFile = new File(workDir, trackingId);
        if (!savedFile.exists()) {
            throw new MediaException("No file found with name: " + trackingId);
        }
        return savedFile;
    }

    public void store(Type type, File file, File thumb) throws IOException {
        File storageDir = fileSystemApplication.getStorageDir(type);
        File thumbnailDir = fileSystemApplication.getThumbnailDir(type);
        FileUtils.copyFileToDirectory(file, storageDir);
        log.info("Stored media file: {}", file.getName());
        FileUtils.copyFileToDirectory(thumb, thumbnailDir);
        log.info("Stored thumbnail file: {}", thumb.getName());
    }

    public File getMedia(Type type, String id) throws IOException {
        File storageDir = fileSystemApplication.getStorageDir(type);
        File contentFile = new File(storageDir, id);
        if (!contentFile.exists()) {
            throw new MediaException("No content found for media id: " + id);
        }
        return contentFile;
    }

    public File getThumbnail(Type type, String id) throws IOException {
        File thumbnailDir = fileSystemApplication.getThumbnailDir(type);
        File thumbFile = new File(thumbnailDir, id);
        if (!thumbFile.exists()) {
            throw new MediaException("No thumbnail found for media id: " + id);
        }
        return thumbFile;
    }

}
