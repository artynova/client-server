package com.nova.cls.network;

public interface Receiver {
    // renamed from receiveMessage, because the method receives packets, not just messages
    void receivePacket();
}
