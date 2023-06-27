package com.nova.cls.data.exceptions.request;

import com.nova.cls.data.exceptions.HttpException;
import com.nova.cls.network.http.Codes;

public class NotFoundException extends HttpException {
    public NotFoundException() {
        super(Codes.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, Codes.NOT_FOUND);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause, Codes.NOT_FOUND);
    }

    public NotFoundException(Throwable cause) {
        super(cause, Codes.NOT_FOUND);
    }
}
