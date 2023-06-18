package com.nova.cls.util;

import com.nova.cls.network.packets.Packet;

import java.nio.ByteBuffer;

public class PacketValidator {

    public static void validateMinPacketRequirements(ByteBuffer buffer) {
        if (buffer.limit() < Packet.MIN_BYTES)
            throw new IllegalArgumentException("Packet with length " + buffer.limit() + " is shorter than min length " + Packet.MIN_BYTES);
        if (buffer.get(0) != Packet.MAGIC_BYTE)
            throw new IllegalArgumentException("Packet does not start with magic byte 13h");
    }

    public static void validatePacket(ByteBuffer buffer, int messageLength) throws IllegalArgumentException {
        if (messageLength < 0)
            throw new IllegalArgumentException("wLen in packet is greater than signed integer limit (and therefore, possible array size limit)");
        short crcHeaders = buffer.getShort(14);
        short crcHeadersComputed = CRC16.crc16(buffer.array(), 0, 14);
        if (crcHeadersComputed != crcHeaders)
            throw new IllegalArgumentException("Packet headers CRC16 does not match expected value");

        short crcMessage = buffer.getShort(16 + messageLength);
        short crcMessageComputed = CRC16.crc16(buffer.array(), 16, messageLength);
        if (crcMessageComputed != crcMessage)
            throw new IllegalArgumentException("Packet message CRC16 does not match expected value");
    }
}
