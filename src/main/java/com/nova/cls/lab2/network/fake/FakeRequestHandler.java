package com.nova.cls.lab2.network.fake;

import com.nova.cls.lab2.logic.Processor;
import com.nova.cls.lab2.network.RequestHandler;
import com.nova.cls.lab2.network.packets.BadPacketException;
import com.nova.cls.lab2.network.packets.Decryptor;
import com.nova.cls.lab2.network.packets.Encryptor;
import com.nova.cls.lab2.network.packets.Packet;

import java.net.InetAddress;

public class FakeRequestHandler implements RequestHandler {
    // all 4 statics are thread-safe
    private static final Decryptor decryptor = new Decryptor();
    private static final Processor processor = new Processor();
    private static final Encryptor encryptor = new Encryptor();
    private static final FakeSender sender = new FakeSender();

    private final byte[] incoming;
    private final InetAddress source;
    private final Object lock = new Object();
    private Packet request;
    private Packet response;

    public FakeRequestHandler(byte[] incoming, InetAddress source) {
        this.incoming = incoming;
        this.source = source;
    }

    // centralizing the entire processing chain here, to be able to supply the sender with both response bytes and target address without concerning intermediate steps with the address
    public void handle() {
        synchronized (lock) {
            try {
                Packet request = decryptor.decrypt(incoming);
                this.request = request;
                Packet response = processor.process(request);
                byte[] outgoing = encryptor.encrypt(response);
                sender.sendPacket(outgoing, source);
                this.response = response;
            } catch (BadPacketException e) {
                System.err.println("Dropping bad request:");
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
