package com.nova.cls.lab2.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    private static final int SHUTDOWN_PHASE_MILLIS = 60000;

    // proper termination of a pool
    public static void shutdown(ExecutorService pool) {
        pool.shutdown();
        try {
            // wait for tasks to finish
            if (!pool.awaitTermination(SHUTDOWN_PHASE_MILLIS, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
                // wait for lingering tasks to terminate
                if (!pool.awaitTermination(SHUTDOWN_PHASE_MILLIS, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Pool did not shut down");
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow(); // if interrupted before this command was reached in the try block
            Thread.currentThread().interrupt();
        }
    }
}
