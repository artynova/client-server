package com.nova.cls.data.exceptions.request;

import com.nova.cls.data.exceptions.HttpException;
import com.nova.cls.network.http.Codes;

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
