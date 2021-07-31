package com.sednar.digital.media.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@ConfigurationProperties("app.video.thumbnail")
@Getter
@Setter
public class VideoProcessingProps {

    private File processorPath;

    private String infoProvider;

    private String generator;

    private String size;

    private String suffix;

    private int grabAtPercent;

    private int gifDuration;

}
