package com.sednar.digital.media.config.mvc;

import com.sednar.digital.media.common.exception.MediaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ValidationException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(
            ValidationException e, WebRequest req) {
        return getError(e, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MediaException.class)
    public ResponseEntity<ErrorDto> handleMediaException(
            MediaException e, WebRequest req) {
        return getError(e, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDto> getError(Exception e, WebRequest req, HttpStatus status) {
        ErrorDto errorDto = ErrorDto.builder()
                .error(e.getMessage())
                .timestamp(new Date())
                .url(req.getDescription(false))
                .build();
        return new ResponseEntity<>(errorDto, status);
    }

}
