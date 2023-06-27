package com.nova.cls.data.exceptions;

public class HttpException extends RuntimeException {
    private final int code;

    public HttpException(int code) {
        this.code = code;
    }

    public HttpException(String message, int code) {
        super(message);
        this.code = code;
    }

    public HttpException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public HttpException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
