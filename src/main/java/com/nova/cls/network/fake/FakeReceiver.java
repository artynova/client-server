package com.nova.cls.network.fake;

import com.nova.cls.data.Command;
import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.Receiver;
import com.nova.cls.network.packets.Encryptor;
import com.nova.cls.network.packets.Message;
import com.nova.cls.network.packets.Packet;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class FakeReceiver implements Receiver {
    private static final InetAddress STUB_ADDRESS = InetAddress.getLoopbackAddress();
    private static boolean verbose = true;
    private final Encryptor encryptor = new Encryptor();
    private final BatchRequestHandler handler;
    private FakeRequestTask lastTask;

    public FakeReceiver(BatchRequestHandler handler) {
        this.handler = handler;
    }

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
        int command = ThreadLocalRandom.current().nextInt(0, Command.values().length); // commands
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
        lastTask = new FakeRequestTask(encryptor.encrypt(request), STUB_ADDRESS);
        if (!handler.offer(lastTask)) {
            System.err.println("Packet dropped due to congestion");
            lastTask = null;
        }
        if (verbose) {
            System.out.println(
                "Time: " + LocalDateTime.now().toLocalTime() + "\nReceiving " + request + "\nFrom " + STUB_ADDRESS
                    + "\n");
        }
    }

    public FakeRequestTask getLastTask() {
        return lastTask;
    }

    @Override
    public void run() {

    }

    @Override
    public void close() throws Exception {

    }
}
