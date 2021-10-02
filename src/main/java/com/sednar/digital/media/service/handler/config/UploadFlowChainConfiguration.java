package com.sednar.digital.media.service.handler.config;

import com.sednar.digital.media.repo.MediaRepository;
import com.sednar.digital.media.repo.UploadProgressRepository;
import com.sednar.digital.media.service.content.image.ImageContentProcessor;
import com.sednar.digital.media.service.content.video.VideoContentProcessor;
import com.sednar.digital.media.service.filesystem.FileSystemClient;
import com.sednar.digital.media.service.handler.chain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadFlowChainConfiguration {

    @Bean
    @Autowired
    public AbstractUploadHandler abstractUploadHandler(
            ImageContentProcessor imageContentProcessor,
            VideoContentProcessor videoContentProcessor,
            UploadProgressRepository uploadProgressRepository,
            MediaRepository mediaRepository,
            FileSystemClient fileSystemClient) {
        AbstractUploadHandler startUploadHandler = new StartUploadHandler(
                imageContentProcessor, videoContentProcessor,
                uploadProgressRepository, mediaRepository, fileSystemClient
        );
        AbstractUploadHandler thumbnailUploadHandler = new ThumbnailUploadHandler(
                imageContentProcessor, videoContentProcessor,
                uploadProgressRepository, mediaRepository, fileSystemClient
        );
        AbstractUploadHandler databaseSaveUploadHandler = new DatabaseSaveUploadHandler(
                imageContentProcessor, videoContentProcessor,
                uploadProgressRepository, mediaRepository, fileSystemClient
        );
        AbstractUploadHandler fileSystemStoreUploadHandler = new FileSystemStoreUploadHandler(
                imageContentProcessor, videoContentProcessor,
                uploadProgressRepository, mediaRepository, fileSystemClient
        );
        AbstractUploadHandler endUploadHandler = new EndUploadHandler(
                imageContentProcessor, videoContentProcessor,
                uploadProgressRepository, mediaRepository, fileSystemClient
        );

        startUploadHandler.setNext(thumbnailUploadHandler);
        thumbnailUploadHandler.setNext(databaseSaveUploadHandler);
        databaseSaveUploadHandler.setNext(fileSystemStoreUploadHandler);
        fileSystemStoreUploadHandler.setNext(endUploadHandler);
        return startUploadHandler;
    }

}
