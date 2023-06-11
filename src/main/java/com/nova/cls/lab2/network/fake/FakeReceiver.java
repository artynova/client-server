package com.nova.cls.lab2.network.fake;

import com.nova.cls.lab2.logic.Processor;
import com.nova.cls.lab2.network.Receiver;
import com.nova.cls.lab2.packets.*;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class FakeReceiver implements Receiver {
    private static final InetAddress STUB_ADDRESS = InetAddress.getLoopbackAddress();
    private static final int MAX_SLEEP_MILLIS = 3000;
    private static boolean verbose = true;
    private final Encryptor encryptor = new Encryptor();
    private final Object completionLock = new Object();
    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Packet lastReceived;
    private Packet lastSent;

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        FakeReceiver.verbose = verbose;
    }

    private static Packet generateRandomPacket() {
        byte source = (byte) ThreadLocalRandom.current().nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);
        long packetId = ThreadLocalRandom.current().nextLong();
        return new Packet(source, packetId, generateRandomMessage());
    }

    private static Message generateRandomMessage() {
        int command = ThreadLocalRandom.current().nextInt();
        int userId = ThreadLocalRandom.current().nextInt();
        StringBuilder bodyBuilder = new StringBuilder(ThreadLocalRandom.current().nextInt(4, 64));
        while (bodyBuilder.length() < bodyBuilder.capacity()) {
            bodyBuilder.append((char) ThreadLocalRandom.current().nextInt('a', 'z' + 1));
        }
        return new Message(command, userId, bodyBuilder.toString());
    }

    public void shutdownPool() {
        pool.shutdown();
    }

    @Override
    public void receivePacket() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(MAX_SLEEP_MILLIS));
        } catch (InterruptedException ignore) {
        }
        Packet request = generateRandomPacket();
        pool.execute(new RequestWorker(encryptor.encrypt(request), STUB_ADDRESS));
        if (verbose) System.out.println("Time: " + LocalDateTime.now().toLocalTime() + "\nReceiving " + request + "\nFrom " + STUB_ADDRESS + "\n");
    }

    public Packet getLastReceived() {
        return lastReceived;
    }

    public Packet getLastSent() {
        return lastSent;
    }

    public Object getCompletionLock() {
        return completionLock;
    }

    private class RequestWorker implements Runnable {
        // all 4 statics are thread-safe
        private static final Decryptor decryptor = new Decryptor();
        private static final Processor processor = new Processor();
        private static final Encryptor encryptor = new Encryptor();
        private static final FakeSender sender = new FakeSender();
        private final byte[] incoming;
        private final InetAddress source;

        public RequestWorker(byte[] packet, InetAddress source) {
            this.incoming = packet;
            this.source = source;
        }

        // centralizing the processing chain here, to be able to supply the sender with both response bytes and target address without concerning intermediate steps with the address
        @Override
        public void run() {
            synchronized (FakeReceiver.this.completionLock) {
                try {
                    Packet request = decryptor.decrypt(incoming);
                    FakeReceiver.this.lastReceived = request;
                    Packet response = processor.process(request);
                    byte[] outgoing = encryptor.encrypt(response);
                    sender.sendPacket(outgoing, source);
                    FakeReceiver.this.lastSent = response;
                } catch (BadPacketException e) {
                    System.err.println("Dropping bad request:");
                    e.printStackTrace();
                    FakeReceiver.this.lastSent = null;
                }
                FakeReceiver.this.completionLock.notifyAll();
            }
        }
    }
}
