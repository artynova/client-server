package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.constants.Codes;

public class ForbiddenException extends HttpException {
    public ForbiddenException() {
        super(Codes.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(message, Codes.FORBIDDEN);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, Codes.FORBIDDEN);
    }

    public ForbiddenException(Throwable cause) {
        super(cause, Codes.FORBIDDEN);
    }
}
