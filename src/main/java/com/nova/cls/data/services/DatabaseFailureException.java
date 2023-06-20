package com.nova.cls.data.services;

/**
 * Represents a non-recoverable database-related error, such as inability to acquire the driver or connect.
 */
public class DatabaseFailureException extends RuntimeException {
    public DatabaseFailureException() {
    }

    public DatabaseFailureException(String message) {
        super(message);
    }

    public DatabaseFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseFailureException(Throwable cause) {
        super(cause);
    }
}
