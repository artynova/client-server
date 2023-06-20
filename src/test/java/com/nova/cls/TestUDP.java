package com.nova.cls;

import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.Client;
import com.nova.cls.network.Server;
import com.nova.cls.network.udp.StoreClientUDP;
import com.nova.cls.network.udp.StoreServerUDP;

public class TestUDP extends TestProtocol {
    public static final int TESTING_THREADS = 16;
    private static final int PACKETS_PER_THREAD = 100;

    @Override
    public int getTestingThreads() {
        return TESTING_THREADS;
    }

    @Override
    public int getPacketsPerThread() {
        return PACKETS_PER_THREAD;
    }

    @Override
    public Server makeServer(BatchRequestHandler handler) {
        return new StoreServerUDP(handler);
    }

    @Override
    public Client makeClient() {
        return new StoreClientUDP();
    }
}
