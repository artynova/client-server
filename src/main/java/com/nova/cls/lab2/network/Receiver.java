package com.nova.cls.lab2.network;

public interface Receiver {
    // renamed from receiveMessage, because the method receives packets, not just messages
    void receivePacket();
}
