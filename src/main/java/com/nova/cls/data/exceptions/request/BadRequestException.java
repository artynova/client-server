package com.nova.cls.data.exceptions.request;

import com.nova.cls.data.exceptions.HttpException;
import com.nova.cls.network.http.Codes;

public class BadRequestException extends HttpException {
    public BadRequestException() {
        super(Codes.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, Codes.BAD_REQUEST);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, Codes.BAD_REQUEST);
    }

    public BadRequestException(Throwable cause) {
        super(cause, Codes.BAD_REQUEST);
    }
}
