package com.nova.cls.lab2.network.fake;

import com.nova.cls.lab2.network.Receiver;

public class FakeServer {
    private static final int RECEIVE_PACKETS = 1000;

    public static void main(String[] args) {
        FakeReceiver.initShared();
        Receiver receiver = new FakeReceiver();
        for (int i = 0; i < RECEIVE_PACKETS; i++) receiver.receivePacket();
        FakeReceiver.shutdownShared();
    }
}
