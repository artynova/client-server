package com.nova.cls;

import com.nova.cls.data.Response;
import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.fake.FakeRequestTask;
import com.nova.cls.network.packets.Message;
import com.nova.cls.network.packets.Packet;
import com.nova.cls.network.tcp.StoreClientTCP;
import com.nova.cls.network.tcp.StoreServerTCP;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestTCP {
    public static final int TESTING_THREADS = 8;
    private static final int PACKETS_PER_THREAD = 10;
    private static StoreServerTCP server;
    private static BatchRequestHandler handler;

    @BeforeClass
    public static void setUpClass() {
        handler = new BatchRequestHandler();
        server = new StoreServerTCP(handler);
        server.start();
    }

    @AfterClass
    public static void tearDownClass() {
        server.close();
        handler.close();
    }

    @Test
    public void testTcp() {
        ExecutorService pool = Executors.newFixedThreadPool(TESTING_THREADS);
        List<Future<Void>> results = new ArrayList<>(TESTING_THREADS);
        for (int i = 0; i < TESTING_THREADS; i++)
            results.add(pool.submit(() -> executeThreadTest(handler)));
        for (int i = 0; i < TESTING_THREADS; i++) {
            try {
                results.get(i).get();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected exception when running thread tests: " + e);
            }
        }
    }

    private Void executeThreadTest(BatchRequestHandler handler) throws Exception {
        StoreClientTCP client = new StoreClientTCP();
        List<FakeRequestTask> tasks = new ArrayList<>(PACKETS_PER_THREAD);
        for (int i = 0; i < PACKETS_PER_THREAD; i++) {
            Packet request = new Packet((byte) 0, i, new Message(1, 155, "Hello world"));
            Packet response = client.send(request);
            assertEquals(request.getSource(), response.getSource());
            assertEquals(request.getPacketId(), response.getPacketId());
            assertEquals(request.getMessage().getUserId(), response.getMessage().getUserId());
            assertEquals(Response.OK.ordinal(), response.getMessage().getMessageType());
            assertEquals("OK", response.getMessage().getBody());
        }
        return null;
    }
}
