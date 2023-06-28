package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.constants.Codes;

public class ConflictException extends HttpException {
    public ConflictException() {
        super(Codes.CONFLICT);
    }

    public ConflictException(String message) {
        super(message, Codes.CONFLICT);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, Codes.CONFLICT);
    }

    public ConflictException(Throwable cause) {
        super(cause, Codes.CONFLICT);
    }
}
