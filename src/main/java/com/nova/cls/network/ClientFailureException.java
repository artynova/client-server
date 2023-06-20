package com.nova.cls.network;

public class ClientFailureException extends RuntimeException {
    public ClientFailureException() {
    }

    public ClientFailureException(String message) {
        super(message);
    }

    public ClientFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientFailureException(Throwable cause) {
        super(cause);
    }
}
