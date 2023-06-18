package com.nova.cls.network.udp;

import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.Receiver;
import com.nova.cls.network.RequestTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class ReceiverUDP implements Receiver, Runnable {
    private final BatchRequestHandler handler;
    private final DatagramSocket socket;
    private final Map<InetSocketAddress, UDPInfo> connectionInfo = new HashMap<>();

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
        return new RequestTaskUDP(packet, getInfoOn(packet));
    }

    private UDPInfo getInfoOn(DatagramPacket packet) {
        return connectionInfo.computeIfAbsent(new InetSocketAddress(packet.getAddress(), packet.getPort()), k -> new UDPInfo(0, System.currentTimeMillis() - 1)); // put with expired session to not consider the packet id
    }

    // listening functionality is bundled into the receiver
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            receivePacket();
        }
    }
}
