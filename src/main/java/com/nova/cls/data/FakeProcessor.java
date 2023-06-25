package com.nova.cls.data;

import com.nova.cls.network.packets.Message;

public class FakeProcessor {
    public Message process(Message request) {
        return new Message(Response.OK, request.getUserId(), "OK");
    }
}
