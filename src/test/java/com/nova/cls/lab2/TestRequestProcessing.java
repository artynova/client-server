package com.nova.cls.lab2;

import com.nova.cls.lab2.network.fake.FakeRequestHandler;
import com.nova.cls.lab2.network.fake.FakeReceiver;
import com.nova.cls.lab2.network.fake.FakeSender;
import com.nova.cls.lab2.packets.Message;
import com.nova.cls.lab2.packets.Packet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestRequestProcessing {
    private static final int TESTING_THREADS = 16;
    private static final int PACKETS_PER_THREAD = 100;
    private static final boolean VERBOSE = true;

    @Test
    public void processing() {
        FakeReceiver.setVerbose(VERBOSE);
        FakeSender.setVerbose(VERBOSE);
        FakeReceiver.initShared();

        ExecutorService pool = Executors.newFixedThreadPool(TESTING_THREADS);
        List<Future<Void>> results = new ArrayList<>(TESTING_THREADS);
        for (int i = 0; i < TESTING_THREADS; i++)
            results.add(pool.submit(this::executeThreadTest));
        for (int i = 0; i < TESTING_THREADS; i++) {
            try {
                results.get(i).get();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected exception when running thread tests: " + e);
            }
        }

        FakeReceiver.shutdownShared();
    }

    private Void executeThreadTest() throws Exception {
        FakeReceiver receiver = new FakeReceiver();
        for (int i = 0; i < PACKETS_PER_THREAD; i++) {
            receiver.receivePacket();
            FakeRequestHandler handler = receiver.getLastHandler();
            synchronized (handler.getLock()) {
                while (!handler.isDone()) handler.getLock().wait();
            }
            compareRequestResponse(handler.getRequest(), handler.getResponse());
        }
        return null;
    }

    private void compareRequestResponse(Packet request, Packet response) {
        if (response == null) fail("Unexpected request processing failure, no response was sent");
        assertEquals(request.getSource(), response.getSource());
        assertEquals(request.getPacketId(), response.getPacketId());
        Message requestMessage = request.getMessage();
        Message responseMessage = response.getMessage();
        assertEquals(requestMessage.getCommandType(), responseMessage.getCommandType());
        assertEquals(requestMessage.getUserId(), responseMessage.getUserId());
        assertEquals("OK", responseMessage.getBody());
    }
}
