package com.nova.cls.network.tcp;

import com.nova.cls.data.FakeProcessor;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.packets.BadPacketException;
import com.nova.cls.network.packets.Decryptor;
import com.nova.cls.network.packets.Encryptor;
import com.nova.cls.network.packets.Packet;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class RequestTaskTCP implements RequestTask {
    private static final Decryptor DECRYPTOR = new Decryptor();
    private static final FakeProcessor PROCESSOR = new FakeProcessor();
    private static final Encryptor ENCRYPTOR = new Encryptor();
    private static final SenderTCP SENDER = new SenderTCP();

    private final byte[] incoming;
    private final SocketChannel clientChannel;

    public RequestTaskTCP(byte[] incoming, SocketChannel clientChannel) {
        this.incoming = incoming;
        this.clientChannel = clientChannel;
    }

    @Override
    public void handle() {
        try {
            Packet request = DECRYPTOR.decrypt(incoming);
            Packet response = PROCESSOR.process(request);
            byte[] outgoing = ENCRYPTOR.encrypt(response);
            SENDER.sendPacket(outgoing, clientChannel);
        } catch (BadPacketException e) {
            System.err.println("Dropping bad packet:");
            e.printStackTrace();
        }
    }
}
