package com.nova.cls.network.tcp;

import com.nova.cls.network.Client;
import com.nova.cls.network.packets.BadPacketException;
import com.nova.cls.network.packets.Decryptor;
import com.nova.cls.network.packets.Encryptor;
import com.nova.cls.network.packets.Message;
import com.nova.cls.network.packets.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeoutException;

/**
 * Client sends and receives information over the network, packets themselves are expected to be created elsewhere.
 */
public class StoreClientTCP implements Client {
    public static final InetSocketAddress SERVER_SOCKET_ADDRESS =
        new InetSocketAddress(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
    public static final int TIMEOUT_MILLIS = 10000;
    public static final int RETRIES = 5;
    public static final int RETRY_INTERVAL_MILLIS = 2000;
    private final Encryptor encryptor = new Encryptor();
    private final Decryptor decryptor = new Decryptor();
    private SocketChannel channel;
    private boolean closed = false;

    public static void main(String[] args) throws TimeoutException, BadPacketException, InterruptedException {
        try (StoreClientTCP clientTCP = new StoreClientTCP()) {
            Packet request = new Packet((byte) 1, 1555, new Message(0, 0, "Hello world"));
            System.out.println("Request: " + request);
            System.out.println("Response: " + clientTCP.send(request));
        } catch (IOException e) {
            System.err.println("Could not close TCP client");
            e.printStackTrace();
        }
    }

    public Packet send(Packet request) throws IOException, InterruptedException, BadPacketException, TimeoutException {
        int tries = 0;
        while (true) { // terminates through timeout exception in catch
            try {
                tryConnect();
                return trySend(request);
            } catch (IOException | TimeoutException e) {
                System.err.println("Could not get a reply from server on try " + (tries + 1));
                if (++tries < RETRIES) {
                    System.err.println("Entering a " + RETRY_INTERVAL_MILLIS + "ms timeout");
                    Thread.sleep(RETRY_INTERVAL_MILLIS);
                    System.err.println("Trying to retransmit...");
                    if (channel != null) {
                        channel.close();
                    }
                    channel =
                        null; // reset channel to avoid issues like channel being connected here yet reset by server
                } else {
                    throw new TimeoutException(e.getMessage());
                }
            }
        }
    }

    private Packet trySend(Packet request) throws IOException, TimeoutException, BadPacketException {
        ByteBuffer buffer = ByteBuffer.wrap(encryptor.encrypt(request));
        channel.write(buffer);
        buffer.clear();

        // get variable length along with first headers
        buffer = ByteBuffer.allocate(Packet.MESSAGE_LENGTH_OFFSET + 4);
        if (channel.socket().getInputStream().read(buffer.array()) == -1) // using socket to make use of soTimeout
        {
            throw new TimeoutException("Connection timed out before response to " + request);
        }
        int messageLength = buffer.getInt(Packet.MESSAGE_LENGTH_OFFSET);

        // create and fill the full message array from already read bytes and the channel
        byte[] response = new byte[messageLength + Packet.BYTES_WITHOUT_MESSAGE];
        System.arraycopy(buffer.array(), 0, response, 0, Packet.MESSAGE_LENGTH_OFFSET + 4);
        buffer = ByteBuffer.wrap(response);
        buffer.position(Packet.MESSAGE_LENGTH_OFFSET + 4);
        if (channel.socket().getInputStream().read(response, buffer.position(), buffer.remaining()) == -1) {
            throw new TimeoutException("Connection timed out before response to " + request);
        }

        return decryptor.decrypt(buffer.array());
    }

    private void tryConnect() throws IOException {
        if (!channelConnected()) {
            channel = SocketChannel.open(SERVER_SOCKET_ADDRESS);
            channel.socket().setSoTimeout(TIMEOUT_MILLIS);
        }
    }

    private boolean channelConnected() {
        return channel != null && channel.isConnected();
    }

    @Override
    public void close() throws IOException {
        if (isClosed()) {
            return;
        }
        if (channel != null) {
            channel.close();
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
