package com.nova.cls.network;

public abstract class BaseServer implements Server {
    public static final int LISTENER_DIE_TIMEOUT_MILLIS = 1000;
    protected final Thread listeningThread;
    protected final Receiver receiver;
    private boolean closed = false;

    public BaseServer(Receiver receiver) {
        this.listeningThread = new Thread(receiver);
        this.receiver = receiver;
    }

    public void start() {
        listeningThread.start();
    }

    public void close() {
        if (isClosed()) return;
        listeningThread.interrupt();
        try {
            listeningThread.join(LISTENER_DIE_TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for listener to die");
        }
        try {
            receiver.close();
        } catch (Exception e) {
            throw new ServerFailureException("Could not close receiver");
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
