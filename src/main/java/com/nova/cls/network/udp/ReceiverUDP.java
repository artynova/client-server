package com.nova.cls.network.udp;

import com.nova.cls.network.BaseReceiver;
import com.nova.cls.network.BatchRequestHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiverUDP extends BaseReceiver {
    private final DatagramSocket socket;

    public ReceiverUDP(BatchRequestHandler handler, int port) throws SocketException {
        super(handler);
        this.socket = new DatagramSocket(port);
    }

    @Override
    protected RequestTaskUDP receiveRequestTask() throws IOException {
        byte[] buffer = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new RequestTaskUDP(packet);
    }
}
