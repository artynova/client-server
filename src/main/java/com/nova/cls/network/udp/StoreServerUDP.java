package com.nova.cls.network.udp;

import com.nova.cls.network.BaseServer;
import com.nova.cls.network.BatchRequestHandler;

import java.net.SocketException;

public class StoreServerUDP extends BaseServer {
    public StoreServerUDP(BatchRequestHandler handler) throws SocketException {
        super(handler, new Thread(new ReceiverUDP(handler)));
    }

    public static void main(String[] args) throws SocketException {
        StoreServerUDP server = new StoreServerUDP(new BatchRequestHandler());
        server.start();
    }
}
