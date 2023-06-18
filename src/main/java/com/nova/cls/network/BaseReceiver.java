package com.nova.cls.network;

public abstract class BaseReceiver implements Receiver, Runnable {
    protected final BatchRequestHandler handler;

    public BaseReceiver(BatchRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public void receivePacket() {
        RequestTask task;
        try {
            task = receiveRequestTask();
        } catch (Exception e) {
            System.err.println("Packet dropped due to an exception:");
            e.printStackTrace();
            return;
        }
        if (!handler.offer(task)) System.err.println("Packet dropped due to congestion");
    }

    protected abstract RequestTask receiveRequestTask() throws Exception;

    // listening functionality is bundled into the receiver
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            receivePacket();
        }
    }
}
