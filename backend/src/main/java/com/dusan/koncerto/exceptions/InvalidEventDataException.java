package com.dusan.koncerto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEventDataException extends RuntimeException {

    public InvalidEventDataException(String message) {
        super(message);
    }

    public InvalidEventDataException(String message, Throwable cause) {
        super(message, cause);
    }
}