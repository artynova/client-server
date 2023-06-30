package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.HttpCode;

public class ConflictException extends HttpException {
    public ConflictException() {
        super(HttpCode.CONFLICT);
    }

    public ConflictException(String message) {
        super(message, HttpCode.CONFLICT);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, HttpCode.CONFLICT);
    }

    public ConflictException(Throwable cause) {
        super(cause, HttpCode.CONFLICT);
    }
}
