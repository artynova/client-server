package com.nova.cls.network;

import com.nova.cls.network.tcp.ReceiverTCP;

import java.io.IOException;

public abstract class BaseServer implements AutoCloseable {
    protected final BatchRequestHandler handler;
    protected final Thread listeningThread;

    public BaseServer(BatchRequestHandler handler, Thread listeningThread) {
        this.handler = handler;
        this.listeningThread = listeningThread;
    }

    public void start() {
        listeningThread.start();
    }

    public void close() {
        handler.shutdown();
        listeningThread.interrupt();
    }
}
