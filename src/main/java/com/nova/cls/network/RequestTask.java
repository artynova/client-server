package com.nova.cls.network;

// handler of a single request (= incoming packet)
public interface RequestTask {
    void handle();
}
