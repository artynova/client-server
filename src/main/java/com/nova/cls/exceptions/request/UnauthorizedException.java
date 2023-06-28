package com.nova.cls.exceptions.request;

import com.nova.cls.exceptions.HttpException;
import com.nova.cls.network.constants.Codes;

public class UnauthorizedException extends HttpException {
    public UnauthorizedException() {
        super(Codes.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(message, Codes.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, Codes.UNAUTHORIZED);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause, Codes.UNAUTHORIZED);
    }
}
