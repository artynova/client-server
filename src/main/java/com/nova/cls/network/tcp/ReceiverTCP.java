package com.nova.cls.network.tcp;

import com.nova.cls.network.BaseReceiver;
import com.nova.cls.network.BatchRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverTCP extends BaseReceiver {
    private final ServerSocket socket;

    public ReceiverTCP(BatchRequestHandler handler, int port) throws IOException {
        super(handler);
        this.socket = new ServerSocket(port);
    }

    @Override
    protected RequestTaskTCP receiveRequestTask() throws IOException {
        Socket clientSocket = socket.accept();
        return new RequestTaskTCP(clientSocket);
    }
}
