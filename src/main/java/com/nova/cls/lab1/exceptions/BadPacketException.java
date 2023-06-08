package com.nova.cls.lab1.exceptions;

public class BadPacketException extends Exception {
    public BadPacketException() {
    }

    public BadPacketException(String message) {
        super(message);
    }

    public BadPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadPacketException(Throwable cause) {
        super(cause);
    }
}
