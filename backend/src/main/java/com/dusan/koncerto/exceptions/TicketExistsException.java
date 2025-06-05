package com.dusan.koncerto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketExistsException extends RuntimeException{

    public TicketExistsException(String message) {
        super(message);
    }

    public TicketExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
