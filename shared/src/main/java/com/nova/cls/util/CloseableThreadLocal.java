package com.nova.cls.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * Wrapper for a ThreadLocal that maintains a queue of created instances and provides a method to deallocate all their resources.
 */
public class CloseableThreadLocal<T extends AutoCloseable> implements AutoCloseable {
    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private final ThreadLocal<T> threadLocal;
    private boolean closed = false;

    public CloseableThreadLocal(Supplier<T> supplier) {
        this.threadLocal = ThreadLocal.withInitial(() -> {
            T t = supplier.get();
            queue.add(t);
            return t;
        });
    }

    public T get() {
        return threadLocal.get();
    }

    public void set(T t) {
        threadLocal.set(t);
    }

    @Override
    public synchronized void close() throws Exception {
        if (isClosed()) {
            return;
        }
        for (T t : queue) {
            t.close(); // try to free all resources in the queue (i.e. all instances of the ThreadLocal
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
