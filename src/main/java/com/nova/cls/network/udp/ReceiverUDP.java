package com.nova.cls.network.udp;

import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.Receiver;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.ServerFailureException;

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
    private boolean closed = false;

    public ReceiverUDP(BatchRequestHandler handler) {
        this.handler = handler;
        try {
            this.socket = new DatagramSocket(Constants.SERVER_PORT);
        } catch (SocketException e) {
            throw new ServerFailureException(e);
        }
    }

    @Override
    public void receivePacket() {
        RequestTask task;
        try {
            try {
                task = receiveRequestTask();
            } catch (SocketException e) {
                if (Thread.currentThread().isInterrupted()) return; // if the thread is interrupted, the listening socket expectedly throws a SocketException
                throw e; // throw exception back for standard logging
            }
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

    @Override
    public void close() throws Exception {
        if (isClosed()) return;
        socket.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
