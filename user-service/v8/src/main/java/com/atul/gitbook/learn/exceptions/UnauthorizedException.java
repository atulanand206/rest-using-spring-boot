package com.atul.gitbook.learn.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ServiceException {

    public UnauthorizedException(String msg) {
        super(msg, HttpStatus.UNAUTHORIZED);
    }
}
