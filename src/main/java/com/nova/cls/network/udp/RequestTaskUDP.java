package com.nova.cls.network.udp;

import com.nova.cls.data.Processor;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.packets.BadPacketException;
import com.nova.cls.network.packets.Decryptor;
import com.nova.cls.network.packets.Encryptor;
import com.nova.cls.network.packets.Packet;

import java.net.DatagramPacket;
import java.util.Arrays;

public class RequestTaskUDP implements RequestTask {
    private static final Decryptor decryptor = new Decryptor();
    private static final Processor processor = new Processor();
    private static final Encryptor encryptor = new Encryptor();
    private static final SenderUDP sender = new SenderUDP();

    private final DatagramPacket packet;

    public RequestTaskUDP(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void handle() {
        try {
            byte[] incoming = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
            Packet request = decryptor.decrypt(incoming);
            Packet response = processor.process(request);
            byte[] outgoing = encryptor.encrypt(response);
            packet.setData(outgoing, 0, outgoing.length); // still retains
            sender.sendPacket(packet);
        } catch (BadPacketException e) {
            System.err.println("Dropping bad packet:");
            e.printStackTrace();
        }

    }
}
