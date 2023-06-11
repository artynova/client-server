package com.nova.cls.lab2.network.fake;

import com.nova.cls.lab2.network.BatchRequestHandler;
import com.nova.cls.lab2.network.Receiver;
import com.nova.cls.lab2.network.packets.Encryptor;
import com.nova.cls.lab2.network.packets.Message;
import com.nova.cls.lab2.network.packets.Packet;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class FakeReceiver implements Receiver {
    private static final InetAddress STUB_ADDRESS = InetAddress.getLoopbackAddress();
    private static boolean verbose = true;
    private static BatchRequestHandler batchHandler;
    private final Encryptor encryptor = new Encryptor();
    private FakeRequestHandler lastHandler;


    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        FakeReceiver.verbose = verbose;
    }

    // initializes the shared batch handler
    public static void initShared(int numThreads) {
        if (batchHandler != null) throw new UnsupportedOperationException("Initializing when already initialized");
        batchHandler = new BatchRequestHandler(numThreads, BatchRequestHandler.DEFAULT_QUEUE_THRESHOLD, BatchRequestHandler.DEFAULT_HANDLE_INTERVAL_MILLIS);
    }

    public static void initShared() {
        initShared(Runtime.getRuntime().availableProcessors());
    }

    // shuts down the shared batch handler
    public static void shutdownShared() {
        if (batchHandler == null) throw new UnsupportedOperationException("Shutting down when not initialized");
        batchHandler.shutdown();
        batchHandler = null;
    }

    private static Packet generateRandomPacket() {
        byte source = (byte) ThreadLocalRandom.current().nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);
        long packetId = ThreadLocalRandom.current().nextLong();
        return new Packet(source, packetId, generateRandomMessage());
    }

    private static Message generateRandomMessage() {
        int command = ThreadLocalRandom.current().nextInt(0, 6); // commands
        int userId = ThreadLocalRandom.current().nextInt();
        StringBuilder bodyBuilder = new StringBuilder(ThreadLocalRandom.current().nextInt(4, 64));
        while (bodyBuilder.length() < bodyBuilder.capacity()) {
            bodyBuilder.append((char) ThreadLocalRandom.current().nextInt('a', 'z' + 1));
        }
        return new Message(command, userId, bodyBuilder.toString());
    }

    @Override
    public void receivePacket() {
        Packet request = generateRandomPacket();
        lastHandler = new FakeRequestHandler(encryptor.encrypt(request), STUB_ADDRESS);
        if (!batchHandler.offer(lastHandler)) {
            System.err.println("Incoming packet dropped due to congestion");
            lastHandler = null;
        }
        if (verbose)
            System.out.println("Time: " + LocalDateTime.now().toLocalTime() + "\nReceiving " + request + "\nFrom " + STUB_ADDRESS + "\n");
    }

    public FakeRequestHandler getLastHandler() {
        return lastHandler;
    }
}
