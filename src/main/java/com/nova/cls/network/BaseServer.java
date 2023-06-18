package com.nova.cls.network;

public abstract class BaseServer implements AutoCloseable {
    protected final Thread listeningThread;

    public BaseServer(Thread listeningThread) {
        this.listeningThread = listeningThread;
    }

    public void start() {
        listeningThread.start();
    }

    public void close() {
        listeningThread.interrupt();
    }
}
