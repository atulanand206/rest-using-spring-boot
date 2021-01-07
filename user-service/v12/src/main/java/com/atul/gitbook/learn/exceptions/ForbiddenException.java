package com.atul.gitbook.learn.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ServiceException {

    public ForbiddenException(String msg) {
        super(msg, HttpStatus.FORBIDDEN);
    }
}
