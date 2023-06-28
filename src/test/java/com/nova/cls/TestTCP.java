//package com.nova.cls;
//
//import com.nova.cls.network.BatchRequestHandler;
//import com.nova.cls.network.Client;
//import com.nova.cls.network.Server;
//import com.nova.cls.network.tcp.StoreClientTCP;
//import com.nova.cls.network.tcp.StoreServerTCP;
//
//public class TestTCP extends TestProtocol {
//    public static final int TESTING_THREADS = 8;
//    private static final int PACKETS_PER_THREAD = 25;
//
//    @Override
//    public int getTestingThreads() {
//        return TESTING_THREADS;
//    }
//
//    @Override
//    public int getPacketsPerThread() {
//        return PACKETS_PER_THREAD;
//    }
//
//    @Override
//    public Server makeServer(BatchRequestHandler handler) {
//        return new StoreServerTCP(handler);
//    }
//
//    @Override
//    public Client makeClient() {
//        return new StoreClientTCP();
//    }
//}
