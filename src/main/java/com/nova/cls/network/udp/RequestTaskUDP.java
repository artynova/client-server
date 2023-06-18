package com.nova.cls.network.udp;

import com.nova.cls.data.FakeProcessor;
import com.nova.cls.data.Response;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.packets.*;

import java.net.DatagramPacket;
import java.util.Arrays;

public class RequestTaskUDP implements RequestTask {
    public static final int MAX_RETRANSMISSION_REQUESTS = 5;
    public static final int SESSION_DURATION_MILLIS = 60000; // number of milliseconds for which the session (i.e., packet order) is tracked
    private static final Decryptor DECRYPTOR = new Decryptor();
    private static final FakeProcessor PROCESSOR = new FakeProcessor();
    private static final Encryptor ENCRYPTOR = new Encryptor();
    private static final SenderUDP SENDER = new SenderUDP();

    private final DatagramPacket packet;
    private final UDPInfo info;

    public RequestTaskUDP(DatagramPacket packet, UDPInfo info) {
        this.packet = packet;
        this.info = info;
        System.out.println(info.getNextPacketId());
        System.out.println(info.getRetransmissionRequests());
        System.out.println(info.getSessionExpireTime());
    }

    @Override
    public void handle() {
        try {
            byte[] incoming = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
            Packet request = DECRYPTOR.decrypt(incoming);
            if (tryRequestRetransmission(request)) return;
            Packet response = PROCESSOR.process(request);
            byte[] outgoing = ENCRYPTOR.encrypt(response);
            packet.setData(outgoing, 0, outgoing.length);
            SENDER.sendPacket(packet);
        } catch (BadPacketException e) {
            System.err.println("Dropping bad packet:");
            e.printStackTrace();
        }
    }

    /**
     * @return whether retransmission request was sent (if yes, task execution is done for now).
     */
    private boolean tryRequestRetransmission(Packet request) {
        synchronized (info) {
            if (info.getNextPacketId() == request.getPacketId() || info.getRetransmissionRequests() >= MAX_RETRANSMISSION_REQUESTS || System.currentTimeMillis() > info.getSessionExpireTime()) {
                System.out.println("ales");
                // consider the packet correct and reset all data
                info.setNextPacketId(request.getPacketId() + 1);
                info.setSessionExpireTime(System.currentTimeMillis() + SESSION_DURATION_MILLIS);
                info.setRetransmissionRequests(0);
                return false;
            }
            packet.setData(makeRetransmissionRequest(info.getNextPacketId(), request));
            SENDER.sendPacket(packet);
            System.out.println("ales sent");
            return true;
        }
    }

    private byte[] makeRetransmissionRequest(long requiredId, Packet request) {
        Message message = new Message(Response.RETRANSMIT_REQUEST, request.getMessage().getUserId(), Long.toUnsignedString(requiredId));
        Packet response = new Packet(request.getSource(), request.getPacketId(), message);
        return ENCRYPTOR.encrypt(response);
    }
}
