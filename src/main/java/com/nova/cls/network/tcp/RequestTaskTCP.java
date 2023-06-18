package com.nova.cls.network.tcp;

import com.nova.cls.data.Processor;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.packets.BadPacketException;
import com.nova.cls.network.packets.Decryptor;
import com.nova.cls.network.packets.Encryptor;
import com.nova.cls.network.packets.Packet;

import java.io.IOException;
import java.net.Socket;

public class RequestTaskTCP implements RequestTask {
    private static final Decryptor decryptor = new Decryptor();
    private static final Processor processor = new Processor();
    private static final Encryptor encryptor = new Encryptor();
    private static final SenderTCP sender = new SenderTCP();

    private final Socket socket;

    public RequestTaskTCP(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void handle() {
        try {
            byte[] incoming = socket.getInputStream().readAllBytes();
            Packet request = decryptor.decrypt(incoming);
            Packet response = processor.process(request);
            byte[] outgoing = encryptor.encrypt(response);
            sender.sendPacket(outgoing, socket);
        } catch (IOException e) {
            System.err.println("Could not accept packet because of an IO problem:");
            e.printStackTrace();
        } catch (BadPacketException e) {
            System.err.println("Dropping bad packet:");
            e.printStackTrace();
        }
    }
}
