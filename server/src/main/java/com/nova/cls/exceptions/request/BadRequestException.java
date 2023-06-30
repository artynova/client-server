package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.HttpCode;

public class BadRequestException extends HttpException {
    public BadRequestException() {
        super(HttpCode.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, HttpCode.BAD_REQUEST);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, HttpCode.BAD_REQUEST);
    }

    public BadRequestException(Throwable cause) {
        super(cause, HttpCode.BAD_REQUEST);
    }
}
