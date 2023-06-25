package com.nova.cls.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class SenderUDP {
    // so that processing threads' responses don't interfere with the listener thread and with each other, each thread has a local datagram socket
    private static final ThreadLocal<DatagramSocket> localSocket = ThreadLocal.withInitial(() -> {
        try {
            return new DatagramSocket();
        } catch (SocketException e) {
            throw new ExceptionInInitializerError(e);
        }
    });

    public void sendPacket(DatagramPacket datagramPacket) {
        try {
            localSocket.get().send(datagramPacket);
        } catch (IOException e) {
            System.err.println("Failed to send outgoing packet:");
            e.printStackTrace();
        }
    }
}
