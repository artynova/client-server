package com.nova.cls;

import com.nova.cls.data.Command;
import com.nova.cls.data.Response;
import com.nova.cls.network.BatchRequestHandler;
import com.nova.cls.network.Client;
import com.nova.cls.network.Server;
import com.nova.cls.network.fake.FakeRequestTask;
import com.nova.cls.network.packets.Message;
import com.nova.cls.network.packets.Packet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class TestProtocol {
    private Server server;
    private BatchRequestHandler handler;

    @Before
    public void setUp() {
        handler = new BatchRequestHandler();
        server = makeServer(handler);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.close();
        handler.close();
    }

    @Test
    public void testOperation() {
        ExecutorService pool = Executors.newFixedThreadPool(getTestingThreads());
        List<Future<Void>> results = new ArrayList<>(getTestingThreads());
        for (int i = 0; i < getTestingThreads(); i++)
            results.add(pool.submit(() -> executeThreadTest(handler)));
        for (int i = 0; i < getTestingThreads(); i++) {
            try {
                results.get(i).get();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected exception when running thread tests: " + e);
            }
        }
    }

    private Void executeThreadTest(BatchRequestHandler handler) throws Exception {
        try (Client client = makeClient()) {
            List<FakeRequestTask> tasks = new ArrayList<>(getPacketsPerThread());
            for (int i = 0; i < getPacketsPerThread(); i++) {
                Packet request = new Packet((byte) 0, i, new Message(Command.GOODS_LIST, 155, "{}"));
                Packet response = client.send(request);
                assertEquals(request.getSource(), response.getSource());
                assertEquals(request.getPacketId(), response.getPacketId());
                assertEquals(request.getMessage().getUserId(), response.getMessage().getUserId());
                assertEquals(Response.OK.ordinal(), response.getMessage().getMessageType());
                assertEquals("[]", response.getMessage().getBody());
            }
            return null;
        }
    }

    public abstract int getTestingThreads();

    public abstract int getPacketsPerThread();


    public abstract Server makeServer(BatchRequestHandler handler);

    public abstract Client makeClient();
}
