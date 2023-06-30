package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.HttpCode;

public class NotFoundException extends HttpException {
    public NotFoundException() {
        super(HttpCode.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, HttpCode.NOT_FOUND);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause, HttpCode.NOT_FOUND);
    }

    public NotFoundException(Throwable cause) {
        super(cause, HttpCode.NOT_FOUND);
    }
}
