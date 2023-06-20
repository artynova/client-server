package com.nova.cls.network.udp;

import com.nova.cls.data.Response;
import com.nova.cls.network.Client;
import com.nova.cls.network.ClientFailureException;
import com.nova.cls.network.packets.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Client sends and receives information over the network, packets themselves are expected to be created elsewhere.
 */
public class StoreClientUDP implements Client {
    public static final int TIMEOUT_MILLIS = 10000;
    public static final int RETRIES = 5;
    public static final int RETRY_INTERVAL_MILLIS = 2000;
    private final Encryptor encryptor = new Encryptor();
    private final Decryptor decryptor = new Decryptor();
    private final DatagramSocket socket;
    private final DatagramPacket packet = new DatagramPacket(new byte[Constants.MAX_PACKET_SIZE], Constants.MAX_PACKET_SIZE, Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
    private final Map<Long, Packet> idsToPackets = new HashMap<>(); // map to keep track of sent packets for retransmission
    private boolean closed = false;

    public StoreClientUDP() {
        try {
            this.socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT_MILLIS);
        } catch (SocketException e) {
            throw new ClientFailureException(e);
        }
    }

    public static void main(String[] args) throws IOException, BadPacketException, InterruptedException {
        try (StoreClientUDP clientUDP = new StoreClientUDP()) {
            Packet request = new Packet((byte) 0, 0, new Message(0, 0, "Hello world"));
            System.out.println("Request: " + request);
            System.out.println("Response: " + clientUDP.send(request));

            request = new Packet((byte) 0, 15, new Message(0, 0, "Hello world"));
            System.out.println("Request: " + request);
            System.out.println("Response: " + clientUDP.send(request));
        } catch (TimeoutException e) {
            System.err.println("Did not manage to get a response");
        }
    }

    public Packet send(Packet request) throws IOException, BadPacketException, TimeoutException, InterruptedException {
        idsToPackets.put(request.getPacketId(), request);
        int tries = 0;
        while (true) { // exit point is with TimeoutException
            try {
                Packet response = trySend(request);
                if (Response.get(response.getMessage().getMessageType()) != Response.RETRANSMIT_REQUEST) {
                    idsToPackets.remove(request.getPacketId()); // if we got a response, this means the server received the packet, and it will not need to be retransmitted
                    return response;
                }
                Packet retransmitPacket = idsToPackets.get(Long.parseLong(request.getMessage().getBody()));
                if (retransmitPacket == null)
                    continue; // if packet is unavailable, keep sending until the server gives up
                send(retransmitPacket);
            } catch (SocketTimeoutException e) {
                System.err.println("Could not get a reply from server on try " + (tries + 1));
                if (++tries < RETRIES) {
                    System.err.println("Entering a " + RETRY_INTERVAL_MILLIS + "ms timeout");
                    Thread.sleep(RETRY_INTERVAL_MILLIS);
                    System.err.println("Trying to retransmit...");
                } else throw new TimeoutException(e.getMessage());
            }
        }
    }

    private Packet trySend(Packet request) throws IOException, BadPacketException {
        byte[] requestBytes = encryptor.encrypt(request);

        packet.setData(requestBytes);
        packet.setAddress(Constants.SERVER_ADDRESS);
        packet.setPort(Constants.SERVER_PORT);
        socket.send(packet);

        packet.setData(new byte[Constants.MAX_PACKET_SIZE]);
        socket.receive(packet);

        byte[] responseBytes = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
        return decryptor.decrypt(responseBytes);
    }

    @Override
    public void close() {
        if (isClosed()) return;
        socket.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
