package com.nova.cls.network.tcp;

import java.io.IOException;
import java.net.Socket;

public class SenderTCP {
    public void sendPacket(byte[] outgoing, Socket socket) {
        try {
            socket.getOutputStream().write(outgoing);
        } catch (IOException e) {
            System.err.println("Failed to send outgoing packet:");
            e.printStackTrace();
        }
        try {
            socket.shutdownOutput();
        }
        catch (IOException e) {
            System.err.println("Failed to shut down socket output:");
            e.printStackTrace();
        }
    }
}
