package com.nova.cls.network.udp;

import com.nova.cls.network.packets.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class StoreClientUDP implements AutoCloseable {
    private final Encryptor encryptor = new Encryptor();
    private final Decryptor decryptor = new Decryptor();
    private final DatagramSocket socket = new DatagramSocket();
    private final DatagramPacket packet = new DatagramPacket(new byte[Constants.MAX_PACKET_SIZE], Constants.MAX_PACKET_SIZE, Constants.SERVER_ADDRESS, Constants.SERVER_PORT);

    public StoreClientUDP() throws SocketException {
    }

    public static void main(String[] args) throws IOException, BadPacketException {
        try (StoreClientUDP clientUDP = new StoreClientUDP()) {
            Packet request = new Packet((byte) 0, 0, new Message(0, 0, "Hello world"));
            System.out.println("Response: " + clientUDP.send(request));
        }
    }

    public Packet send(Packet request) throws IOException, BadPacketException {
        byte[] requestBytes = encryptor.encrypt(request);

        packet.setData(requestBytes);
        socket.send(packet);

        packet.setData(new byte[Constants.MAX_PACKET_SIZE]);
        socket.receive(packet);

        byte[] responseBytes = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
        return decryptor.decrypt(responseBytes);
    }

    @Override
    public void close() {
        socket.close();
    }
}
