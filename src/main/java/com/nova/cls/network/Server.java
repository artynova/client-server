package com.nova.cls.network;

public interface Server extends AutoCloseable {
    /**
     * Is not required to succeed when called on a server that previously ran and was closed.
     */
    void start() throws ServerFailureException;
}
