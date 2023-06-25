package com.nova.cls.network.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SenderTCP {
    public void sendPacket(byte[] outgoing, SocketChannel clientChannel) {
        try {
            // cannot send more than one response at a time
            synchronized (clientChannel) {
                if (!clientChannel.isConnected()) {
                    return;
                }
                clientChannel.write(ByteBuffer.wrap(outgoing));
            }
        } catch (IOException e) {
            System.err.println("Failed to send outgoing packet:");
            e.printStackTrace();
        }
    }
}
