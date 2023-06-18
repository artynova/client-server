package com.nova.cls.network.udp;

import com.nova.cls.data.FakeProcessor;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.packets.BadPacketException;
import com.nova.cls.network.packets.Decryptor;
import com.nova.cls.network.packets.Encryptor;
import com.nova.cls.network.packets.Packet;

import java.net.DatagramPacket;
import java.util.Arrays;

public class RequestTaskUDP implements RequestTask {
    private static final Decryptor DECRYPTOR = new Decryptor();
    private static final FakeProcessor PROCESSOR = new FakeProcessor();
    private static final Encryptor ENCRYPTOR = new Encryptor();
    private static final SenderUDP SENDER = new SenderUDP();

    private final DatagramPacket packet;

    public RequestTaskUDP(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void handle() {
        try {
            byte[] incoming = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
            Packet request = DECRYPTOR.decrypt(incoming);
            Packet response = PROCESSOR.process(request);
            byte[] outgoing = ENCRYPTOR.encrypt(response);
            packet.setData(outgoing, 0, outgoing.length); // still retains
            SENDER.sendPacket(packet);
        } catch (BadPacketException e) {
            System.err.println("Dropping bad packet:");
            e.printStackTrace();
        }

    }
}
