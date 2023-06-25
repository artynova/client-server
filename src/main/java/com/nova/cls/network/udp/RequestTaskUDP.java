package com.nova.cls.network.udp;

import com.nova.cls.data.Processor;
import com.nova.cls.data.Response;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.packets.BadPacketException;
import com.nova.cls.network.packets.Decryptor;
import com.nova.cls.network.packets.Encryptor;
import com.nova.cls.network.packets.Message;
import com.nova.cls.network.packets.Packet;

import java.net.DatagramPacket;
import java.util.Arrays;

public class RequestTaskUDP implements RequestTask {
    public static final int MAX_RETRANSMISSION_REQUESTS = 5;
    public static final int SESSION_DURATION_MILLIS = 60000;
    // number of milliseconds for which the session (i.e., packet order) is tracked
    private static final Decryptor DECRYPTOR = new Decryptor();
    private static final Processor PROCESSOR = new Processor();
    private static final Encryptor ENCRYPTOR = new Encryptor();
    private static final SenderUDP SENDER = new SenderUDP();

    private final DatagramPacket packet;
    private final UDPInfo info;

    public RequestTaskUDP(DatagramPacket packet, UDPInfo info) {
        this.packet = packet;
        this.info = info;
    }

    @Override
    public void handle() {
        try {
            byte[] incoming =
                Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
            Packet request = DECRYPTOR.decrypt(incoming);
            if (tryRequestRetransmission(request)) {
                return;
            }
            Message responseMessage = PROCESSOR.process(request.getMessage());
            Packet response = new Packet(request.getSource(), request.getPacketId(), responseMessage);
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
            if (info.getNextPacketId() == request.getPacketId()
                || info.getRetransmissionRequests() >= MAX_RETRANSMISSION_REQUESTS
                || System.currentTimeMillis() > info.getSessionExpireTime()) {
                // consider the packet correct and reset all data
                info.setNextPacketId(request.getPacketId() + 1);
                info.setSessionExpireTime(System.currentTimeMillis() + SESSION_DURATION_MILLIS);
                info.setRetransmissionRequests(0);
                return false;
            }
            packet.setData(makeRetransmissionRequest(info.getNextPacketId(), request));
            SENDER.sendPacket(packet);
            return true;
        }
    }

    private byte[] makeRetransmissionRequest(long requiredId, Packet request) {
        Message message = new Message(Response.RETRANSMIT_REQUEST, request.getMessage().getUserId(),
            Long.toUnsignedString(requiredId));
        Packet response = new Packet(request.getSource(), request.getPacketId(), message);
        return ENCRYPTOR.encrypt(response);
    }
}
