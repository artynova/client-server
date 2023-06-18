package com.nova.cls.network.udp;

import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.Receiver;
import com.nova.cls.network.RequestTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiverUDP implements Receiver, Runnable {
    private final BatchRequestHandler handler;
    private final DatagramSocket socket;

    public ReceiverUDP(BatchRequestHandler handler) throws SocketException {
        this.handler = handler;
        this.socket = new DatagramSocket(Constants.SERVER_PORT);
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
    private RequestTaskUDP receiveRequestTask() throws IOException {
        byte[] buffer = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new RequestTaskUDP(packet);
    }

    // listening functionality is bundled into the receiver
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            receivePacket();
        }
    }
}
