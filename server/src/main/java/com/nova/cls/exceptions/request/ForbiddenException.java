package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.HttpCode;

public class ForbiddenException extends HttpException {
    public ForbiddenException() {
        super(HttpCode.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(message, HttpCode.FORBIDDEN);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, HttpCode.FORBIDDEN);
    }

    public ForbiddenException(Throwable cause) {
        super(cause, HttpCode.FORBIDDEN);
    }
}
