package com.nova.cls.util;

public class CipherException extends SecurityException {
    public CipherException() {
    }

    public CipherException(String msg) {
        super(msg);
    }

    public CipherException(String message, Throwable cause) {
        super(message, cause);
    }

    public CipherException(Throwable cause) {
        super(cause);
    }
}
