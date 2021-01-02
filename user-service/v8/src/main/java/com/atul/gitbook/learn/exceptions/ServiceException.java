package com.atul.gitbook.learn.exceptions;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class ServiceException extends RuntimeException {

    private final HttpStatus fStatus;

    public ServiceException(final String msg, final HttpStatus status) {
        super(msg);
        fStatus = status;
    }

    public HttpStatus getStatus() {
        return fStatus;
    }
}
