package com.nova.cls.network;

import com.nova.cls.network.packets.Packet;

public interface Client extends AutoCloseable {
    Packet send(Packet request) throws Exception;
}
