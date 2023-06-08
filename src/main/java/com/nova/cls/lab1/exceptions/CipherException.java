package com.nova.cls.lab1.exceptions;

import java.security.GeneralSecurityException;

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
