package com.nova.cls.network.tcp;

import com.nova.cls.network.BaseServer;
import com.nova.cls.network.BatchRequestHandler;

import java.io.IOException;

public class StoreServerTCP extends BaseServer {
    public StoreServerTCP(BatchRequestHandler handler) {
        super(new Thread(new ReceiverTCP(handler)));
    }

    public static void main(String[] args) throws IOException {
        StoreServerTCP server = new StoreServerTCP(new BatchRequestHandler());
        server.start();
    }
}
