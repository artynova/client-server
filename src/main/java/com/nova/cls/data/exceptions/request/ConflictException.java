package com.nova.cls.data.exceptions.request;

import com.nova.cls.data.exceptions.HttpException;
import com.nova.cls.network.http.Codes;

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
