package com.nova.cls.exceptions;

public class NoConnectionException extends RuntimeException {
    public NoConnectionException() {
    }

    public NoConnectionException(String message) {
        super(message);
    }

    public NoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoConnectionException(Throwable cause) {
        super(cause);
    }
}
