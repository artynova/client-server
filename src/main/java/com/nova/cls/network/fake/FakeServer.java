package com.nova.cls.network.fake;

import com.nova.cls.network.Receiver;
import com.nova.cls.network.BatchRequestHandler;

public class FakeServer {
    private static final int RECEIVE_PACKETS = 1000;

    public static void main(String[] args) {
        BatchRequestHandler handler = new BatchRequestHandler();
        Receiver receiver = new FakeReceiver(handler);
        for (int i = 0; i < RECEIVE_PACKETS; i++) receiver.receivePacket();
        handler.shutdown();
    }
}
