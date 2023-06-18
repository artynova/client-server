package com.nova.cls.network.tcp;

import com.nova.cls.network.packets.*;

import java.io.IOException;
import java.net.Socket;

public class StoreClientTCP implements AutoCloseable {
    private final Encryptor encryptor = new Encryptor();
    private final Decryptor decryptor = new Decryptor();
    private Socket socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);

    public StoreClientTCP() throws IOException {
    }

    public static void main(String[] args) throws IOException, BadPacketException {
        try (StoreClientTCP clientTCP = new StoreClientTCP()) {
            Packet request = new Packet((byte) 0, 0, new Message(0, 0, "Hello world"));
            System.out.println("Response: " + clientTCP.send(request));
        } catch (IOException e) {
            System.err.println("Could not close TCP client");
            e.printStackTrace();
        }
    }

    public Packet send(Packet request) throws IOException, BadPacketException {
        byte[] requestBytes = encryptor.encrypt(request);

        socket.getOutputStream().write(requestBytes);
        socket.shutdownOutput();

        byte[] responseBytes = socket.getInputStream().readAllBytes();

        return decryptor.decrypt(responseBytes);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
