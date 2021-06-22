package com.sednar.digital.media.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.image.thumbnail")
@Getter
@Setter
public class ImageContentProcessingProps {

    private int width;

    private int height;

    private String suffix;

}
