package com.nova.cls.lab2.network;

import com.nova.cls.lab2.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchRequestHandler {
    public static final int DEFAULT_QUEUE_THRESHOLD = 50;
    public static final int DEFAULT_HANDLE_INTERVAL_MILLIS = 100;
    private static final int BUFFER_MULTIPLIER = 10;
    private final ExecutorService pool;
    private final ArrayBlockingQueue<RequestHandler> queue;
    private final int queueThreshold;
    private final Timer timer;

    public BatchRequestHandler(int threads, int queueThreshold, int handleIntervalMillis) {
        if (threads < 1) throw new IllegalArgumentException("Threads < 1: " + threads);
        if (queueThreshold < 1) throw new IllegalArgumentException("Queue threshold < 1: " + queueThreshold);
        if (handleIntervalMillis < 1)
            throw new IllegalArgumentException("Handle interval (in milliseconds) < 1: " + handleIntervalMillis);
        this.pool = Executors.newFixedThreadPool(threads);
        this.queueThreshold = queueThreshold;
        this.queue = new ArrayBlockingQueue<>(queueThreshold * threads * BUFFER_MULTIPLIER); // extra buffer space is for congestion spikes

        this.timer = new Timer();
        TimerTask regularTask = new BatchHandlingTimerTask();
        this.timer.scheduleAtFixedRate(regularTask, handleIntervalMillis, handleIntervalMillis);
    }

    public BatchRequestHandler() {
        this(Runtime.getRuntime().availableProcessors(), DEFAULT_QUEUE_THRESHOLD, DEFAULT_HANDLE_INTERVAL_MILLIS);
    }

    public void shutdown() {
        ThreadUtils.shutdown(pool);
        timer.cancel();
    }

    public boolean offer(RequestHandler handler) {
        boolean accepted = queue.offer(handler);
        if (!accepted) return false;
        if (queue.size() >= queueThreshold) {
            handleBatch();
        }
        return true;
    }

    private void handleBatch() {
        pool.execute(() -> {
            List<RequestHandler> batch = new ArrayList<>(queueThreshold);
            queue.drainTo(batch, queueThreshold);
            for (RequestHandler handler : batch) {
                handler.handle();
            }
        });
    }

    private class BatchHandlingTimerTask extends TimerTask {
        @Override
        public void run() {
            if (!queue.isEmpty()) {
                BatchRequestHandler.this.handleBatch();
            }
        }
    }
}
