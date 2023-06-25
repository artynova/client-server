package com.nova.cls.network.tcp;

import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.Receiver;
import com.nova.cls.network.RequestTask;
import com.nova.cls.network.ServerFailureException;
import com.nova.cls.network.packets.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;

public class ReceiverTCP implements Receiver, Runnable {
    public static final int TIMEOUT_MILLIS = 20000;
    private static final int BUFFER_SIZE = 1024;
    private final Map<SocketChannel, List<Byte>> clientPartials = new HashMap<>();
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final ByteBuffer buffer;
    private final BatchRequestHandler handler;
    private boolean closed = false;


    public ReceiverTCP(BatchRequestHandler handler) {
        this.handler = handler;
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(Constants.SERVER_ADDRESS, Constants.SERVER_PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
        } catch (IOException e) {
            throw new ServerFailureException("Could not instantiate ReceiverTCP: " + e.getMessage(), e);
        }
    }

    public void receivePacket() {
        try {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keys = selectedKeys.iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                updateKeyRelevance(key);
                if (key.isValid() && key.isAcceptable()) {
                    register(selector, serverChannel);
                }
                if (key.isValid() && key.isReadable()) {
                    readRequests(buffer, key);
                }

                keys.remove();
            }
        } catch (IOException e) {
            throw new ServerFailureException("Failed while receiving packets: " + e.getMessage(), e);
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel clientChannel = serverSocket.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, OP_READ, System.currentTimeMillis());
    }

    private void readRequests(ByteBuffer buffer, SelectionKey key) throws IOException {
        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            int r = clientChannel.read(buffer);
            if (r == -1) {
                key.cancel();
                clientChannel.close();
                return;
            }

            buffer.flip();
            updateClientPartial(clientChannel, buffer);
            processMessages(clientChannel);
            buffer.clear();
        } catch (SocketException e) { // guard against connection resets
            key.channel().close();
        }
    }

    private void updateClientPartial(SocketChannel clientChannel, ByteBuffer buffer) {
        List<Byte> storedPartial = clientPartials.computeIfAbsent(clientChannel, k -> new ArrayList<>());
        while (buffer.remaining() > 0) {
            storedPartial.add(buffer.get());
        }
    }

    private void processMessages(SocketChannel clientChannel) {
        List<Byte> storedPartial = clientPartials.get(clientChannel);
        byte[] bytes = new byte[storedPartial.size()];
        for (int i = 0; i < storedPartial.size(); i++) {
            bytes[i] = storedPartial.get(i);
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        // while there are messages long enough to figure out expected length
        while (buffer.remaining() >= Packet.MESSAGE_LENGTH_OFFSET + 4) {
            int length = buffer.getInt(buffer.position() + Packet.MESSAGE_LENGTH_OFFSET);
            // if full message is not available, finish
            if (buffer.remaining() < length + Packet.BYTES_WITHOUT_MESSAGE) {
                break;
            }

            byte[] packet = new byte[length + Packet.BYTES_WITHOUT_MESSAGE];
            buffer.get(packet);
            submitTask(packet, clientChannel);
        }
        // store the remainder
        storedPartial.clear();
        while (buffer.remaining() > 0) {
            storedPartial.add(buffer.get());
        }
    }

    private void submitTask(byte[] request, SocketChannel clientChannel) {
        RequestTask task;
        try {
            task = new RequestTaskTCP(request, clientChannel);
        } catch (Exception e) {
            System.err.println("Packet dropped due to an exception:");
            e.printStackTrace();
            return;
        }
        if (!handler.offer(task)) {
            System.err.println("Packet dropped due to congestion");
        }
    }

    private void updateKeyRelevance(SelectionKey key) throws IOException {
        if (!key.isValid() || !(key.channel() instanceof SocketChannel clientChannel)) {
            return;
        }

        long lastActivityTime = (Long) key.attachment();
        long now = System.currentTimeMillis();

        if (now - lastActivityTime > TIMEOUT_MILLIS) {
            key.cancel();
            clientChannel.close();
            return;
        }
        key.attach(now);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            receivePacket();
        }
    }

    @Override
    public void close() throws ServerFailureException {
        if (isClosed()) {
            return;
        }
        try {
            selector.close();
        } catch (IOException e) {
            throw new ServerFailureException("Error while closing selector:");
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
