package com.nova.cls.exceptions;

public class RequestFailureException extends Exception {
    private final int code;

    public RequestFailureException(int code) {
        super("Error " + code);
        this.code = code;
    }

    public RequestFailureException(String message, int code) {
        super("Error " + code + ": " + message);
        this.code = code;
    }

    public RequestFailureException(String message, Throwable cause, int code) {
        super("Error " + code + ": " + message, cause);
        this.code = code;
    }

    public RequestFailureException(Throwable cause, int code) {
        super("Error " + code, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
