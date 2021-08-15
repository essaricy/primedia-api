package com.sednar.digital.media.common.exception;

public class MediaException extends RuntimeException {

    public MediaException(String message) {
        super(message);
    }

    public MediaException(String message, Throwable t) {
        super(message, t);
    }

}
