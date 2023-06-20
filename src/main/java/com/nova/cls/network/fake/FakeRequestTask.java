package com.nova.cls.network.fake;

import com.nova.cls.network.packets.*;
import com.nova.cls.data.FakeProcessor;
import com.nova.cls.network.RequestTask;

import java.net.InetAddress;

public class FakeRequestTask implements RequestTask {
    // all 4 statics are thread-safe
    private static final Decryptor decryptor = new Decryptor();
    private static final FakeProcessor FAKE_PROCESSOR = new FakeProcessor();
    private static final Encryptor encryptor = new Encryptor();
    private static final FakeSender sender = new FakeSender();

    private final byte[] incoming;
    private final InetAddress source;
    private final Object lock = new Object();
    private Packet request;
    private Packet response;

    public FakeRequestTask(byte[] incoming, InetAddress source) {
        this.incoming = incoming;
        this.source = source;
    }

    // centralizing the entire processing chain here, to be able to supply the sender with both response bytes and target address without concerning intermediate steps with the address
    public void handle() {
        synchronized (lock) {
            try {
                Packet request = decryptor.decrypt(incoming);
                this.request = request;
                Message responseMessage = FAKE_PROCESSOR.process(request.getMessage());
                Packet response = new Packet(request.getSource(), request.getPacketId(), responseMessage);
                byte[] outgoing = encryptor.encrypt(response);
                sender.sendPacket(outgoing, source);
                this.response = response;
            } catch (BadPacketException e) {
                System.err.println("Dropping bad packet:");
                e.printStackTrace();
            }
            lock.notifyAll();
        }
    }

    public Packet getRequest() {
        return request;
    }

    public Packet getResponse() {
        return response;
    }

    public boolean isDone() {
        return response != null;
    }

    public Object getLock() {
        return lock;
    }
}
