package com.nova.cls.network.packets;

import java.math.BigInteger;
import java.util.Objects;

// numbers are interpreted as unsigned
public final class Packet {
    public static final byte MAGIC_BYTE = 0x13;
    public static final int MESSAGE_LENGTH_OFFSET = 10;
    public static final int BYTES_WITHOUT_MESSAGE = 18;
    public static final int MIN_BYTES = 42; // 26 total bytes for known-size fields, 16 is the minimal properly encrypted message body

    private final byte source;
    private final long packetId;
    private final Message message;

    public Packet(byte source, long packetId, Message message) {
        this.source = source;
        this.packetId = packetId;
        this.message = message;
    }

    public byte getSource() {
        return source;
    }

    public short getSourceUnsigned() {
        return (short) Byte.toUnsignedInt(source); // byte cannot represent an unsigned value that will not fit in a short
    }

    public long getPacketId() {
        return packetId;
    }

    public BigInteger getPacketIdUnsigned() {
        return new BigInteger(Long.toUnsignedString(packetId));
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Packet packet = (Packet) o;
        return getSource() == packet.getSource() && getPacketId() == packet.getPacketId() && getMessage().equals(packet.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSource(), getPacketId(), getMessage());
    }

    @Override
    public String toString() {
        return "Packet { source = " + getSourceUnsigned() +
                ", packetId = " + getPacketIdUnsigned() +
                ", message = " + getMessage() +
                " }";
    }
}
