package com.sednar.digital.media.config.mvc;

import com.sednar.digital.media.common.exception.MediaException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;

@RestController
@RequestMapping("/test/exception")
public class TestExceptionHandlerResource {

    @GetMapping("/validation")
    public void throwValidationException() {
        throw new ValidationException("validation exception");
    }

    @GetMapping("/media")
    public void throwMediaException() {
        throw new MediaException("media exception");
    }

}
