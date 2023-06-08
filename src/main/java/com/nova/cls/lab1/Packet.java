package com.nova.cls.lab1;

import com.nova.cls.lab1.validators.NumberValidator;

import java.math.BigInteger;

public final class Packet {
    private final short source;
    private final BigInteger packetId;
    private final Message message;

    public Packet(short source, BigInteger packetId, Message message) {
        NumberValidator.validateUnsignedByte(source);
        NumberValidator.validateUnsignedLong(packetId);
        this.source = source;
        this.packetId = packetId;
        this.message = message;
    }

    public short getSource() {
        return source;
    }

    public BigInteger getPacketId() {
        return packetId;
    }

    public Message getMessage() {
        return message;
    }
}
