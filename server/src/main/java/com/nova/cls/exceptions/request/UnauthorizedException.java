package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.HttpCode;

public class UnauthorizedException extends HttpException {
    public UnauthorizedException() {
        super(HttpCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(message, HttpCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, HttpCode.UNAUTHORIZED);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause, HttpCode.UNAUTHORIZED);
    }
}
