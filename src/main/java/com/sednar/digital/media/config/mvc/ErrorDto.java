package com.sednar.digital.media.config.mvc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class ErrorDto {

    private Date timestamp;

    private String url;

    private String error;

}
