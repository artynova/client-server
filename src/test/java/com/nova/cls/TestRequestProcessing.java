//package com.nova.cls;
//
//import com.nova.cls.data.Response;
//import com.nova.cls.network.BatchRequestHandler;
//import com.nova.cls.network.fake.FakeReceiver;
//import com.nova.cls.network.fake.FakeRequestTask;
//import com.nova.cls.network.fake.FakeSender;
//import com.nova.cls.network.packets.Message;
//import com.nova.cls.network.packets.Packet;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.fail;
//
//public class TestRequestProcessing {
//    private static final int TESTING_THREADS = 25;
//    private static final int PACKETS_PER_THREAD = 100;
//
//    private static final int MAX_HANDLER_THREADS = 8;
//    private static final int HANDLER_BATCH_SIZE = 50;
//    private static final int HANDLER_INTERVAL_MILLIS = 50;
//
//    private static final boolean VERBOSE = false;
//
//
//    @Test
//    public void testProcessing() {
//        FakeReceiver.setVerbose(VERBOSE);
//        FakeSender.setVerbose(VERBOSE);
//
//        BatchRequestHandler handler =
//            new BatchRequestHandler(MAX_HANDLER_THREADS, HANDLER_BATCH_SIZE, HANDLER_INTERVAL_MILLIS);
//        ExecutorService pool = Executors.newFixedThreadPool(TESTING_THREADS);
//        List<Future<Void>> results = new ArrayList<>(TESTING_THREADS);
//        for (int i = 0; i < TESTING_THREADS; i++) {
//            results.add(pool.submit(() -> executeThreadTest(handler)));
//        }
//        for (int i = 0; i < TESTING_THREADS; i++) {
//            try {
//                results.get(i).get();
//            } catch (Exception e) {
//                e.printStackTrace();
//                handler.close();
//                fail("Unexpected exception when running thread tests: " + e);
//            }
//        }
//
//        handler.close();
//    }
//
//    private Void executeThreadTest(BatchRequestHandler handler) throws Exception {
//        FakeReceiver receiver = new FakeReceiver(handler);
//        List<FakeRequestTask> tasks = new ArrayList<>(PACKETS_PER_THREAD);
//        // schedule all tasks
//        for (int i = 0; i < PACKETS_PER_THREAD; i++) {
//            receiver.receivePacket();
//            FakeRequestTask task = receiver.getLastTask();
//            assertNotEquals("Request unexpectedly dropped due to congestion", null, task);
//            tasks.add(task);
//        }
//        // get results from all handlers
//        for (FakeRequestTask task : tasks) {
//            synchronized (task.getLock()) {
//                while (!task.isDone()) {
//                    task.getLock().wait();
//                }
//            }
//            compareRequestResponse(task.getRequest(), task.getResponse());
//        }
//        return null;
//    }
//
//    private void compareRequestResponse(Packet request, Packet response) {
//        assertNotEquals("Unexpected request processing failure, no response was sent", null, response);
//        assertEquals(request.getSource(), response.getSource());
//        assertEquals(request.getPacketId(), response.getPacketId());
//        Message requestMessage = request.getMessage();
//        Message responseMessage = response.getMessage();
//        assertEquals(Response.OK.ordinal(), responseMessage.getMessageType());
//        assertEquals(requestMessage.getUserId(), responseMessage.getUserId());
//        assertEquals("OK", responseMessage.getBody());
//    }
//}
