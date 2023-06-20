package com.nova.cls.network;

import com.nova.cls.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// common pool where incoming requests are accumulated and regularly serviced in batches (also employing multiple threads when there are a lot of requests)
public class BatchRequestHandler implements AutoCloseable {
    public static final int DEFAULT_THREADS = 4;
    public static final int DEFAULT_MAX_BATCH_SIZE = 50;
    public static final int DEFAULT_HANDLE_INTERVAL_MILLIS = 100;
    private static final int BUFFER_MULTIPLIER = 10;
    private final ExecutorService pool;
    private final ArrayBlockingQueue<RequestTask> queue;
    private final int maxBatchSize;
    private final Timer timer;
    private boolean closed = false;

    public BatchRequestHandler(int threads, int maxBatchSize, int handleIntervalMillis) {
        if (threads < 1) throw new IllegalArgumentException("Threads < 1: " + threads);
        if (maxBatchSize < 1) throw new IllegalArgumentException("Max batch size < 1: " + maxBatchSize);
        if (handleIntervalMillis < 1)
            throw new IllegalArgumentException("Handle interval (in milliseconds) < 1: " + handleIntervalMillis);
        pool = Executors.newFixedThreadPool(threads);
        this.maxBatchSize = maxBatchSize;
        queue = new ArrayBlockingQueue<>(maxBatchSize * threads * BUFFER_MULTIPLIER); // extra buffer space is for congestion spikes

        timer = new Timer();
        timer.scheduleAtFixedRate(new BatchHandlingTimerTask(), handleIntervalMillis, handleIntervalMillis);
    }

    public BatchRequestHandler() {
        this(DEFAULT_THREADS, DEFAULT_MAX_BATCH_SIZE, DEFAULT_HANDLE_INTERVAL_MILLIS);
    }

    @Override
    public void close() {
        if (isClosed()) return;
        ThreadUtils.shutdown(pool);
        timer.cancel();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean offer(RequestTask handler) {
        if (closed)
            throw new UnsupportedOperationException("Trying to offer a task to a shutdown batch request handler");
        if (!queue.offer(handler)) return false;
        if (queue.size() >= maxBatchSize) handleBatch();
        return true;
    }

    private void handleBatch() {
        pool.execute(() -> {
            List<RequestTask> batch = new ArrayList<>(maxBatchSize);
            queue.drainTo(batch, maxBatchSize);
            for (RequestTask handler : batch) {
                handler.handle();
            }
        });
    }

    private class BatchHandlingTimerTask extends TimerTask {
        @Override
        public void run() {
            if (!queue.isEmpty()) {
                handleBatch();
            }
        }
    }
}
