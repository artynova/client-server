package com.nova.cls.network.tcp;

import com.nova.cls.network.BaseServer;
import com.nova.cls.network.BatchRequestHandler;

import java.io.IOException;

public class StoreServerTCP extends BaseServer {
    public static final int PORT = 8080;

    public StoreServerTCP(BatchRequestHandler handler) throws IOException {
        super(handler, new Thread(new ReceiverTCP(handler)));
    }

    public static void main(String[] args) throws IOException {
        StoreServerTCP server = new StoreServerTCP(new BatchRequestHandler());
        server.start();
    }

    public void start() {
        listeningThread.start();
    }

    @Override
    public void close() {
        handler.shutdown();
        listeningThread.interrupt();
    }

}
