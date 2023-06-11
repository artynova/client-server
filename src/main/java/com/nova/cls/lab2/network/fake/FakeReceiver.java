package com.nova.cls.lab2.network.fake;

import com.nova.cls.lab2.network.Receiver;
import com.nova.cls.lab2.network.packets.Encryptor;
import com.nova.cls.lab2.network.packets.Message;
import com.nova.cls.lab2.network.packets.Packet;
import com.nova.cls.lab2.util.ThreadUtils;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class FakeReceiver implements Receiver {
    private static final InetAddress STUB_ADDRESS = InetAddress.getLoopbackAddress();
    private static boolean verbose = true;
    private static ExecutorService pool;
    private final Encryptor encryptor = new Encryptor();
    private FakeRequestHandler lastHandler;


    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        FakeReceiver.verbose = verbose;
    }

    // initializes the shared thread pool
    public static void initShared(int numThreads) {
        if (pool != null) throw new UnsupportedOperationException("Initializing when already initialized");
        pool = Executors.newFixedThreadPool(numThreads);
    }

    public static void initShared() {
        initShared(Runtime.getRuntime().availableProcessors());
    }

    // shuts down the shared thread pool
    public static void shutdownShared() {
        if (pool == null) throw new UnsupportedOperationException("Shutting down when not initialized");
        ThreadUtils.shutdown(pool);
        pool = null;
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

    @Override
    public void receivePacket() {
        Packet request = generateRandomPacket();
        lastHandler = new FakeRequestHandler(encryptor.encrypt(request), STUB_ADDRESS);
        pool.execute(lastHandler::handle);
        if (verbose)
            System.out.println("Time: " + LocalDateTime.now().toLocalTime() + "\nReceiving " + request + "\nFrom " + STUB_ADDRESS + "\n");
    }

    public FakeRequestHandler getLastHandler() {
        return lastHandler;
    }
}
