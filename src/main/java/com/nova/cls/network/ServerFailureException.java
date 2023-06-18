package com.nova.cls.network;

public class ServerFailureException extends RuntimeException {
    public ServerFailureException() {
    }

    public ServerFailureException(String message) {
        super(message);
    }

    public ServerFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerFailureException(Throwable cause) {
        super(cause);
    }
}
