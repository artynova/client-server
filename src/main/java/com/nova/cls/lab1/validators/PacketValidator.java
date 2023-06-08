package com.nova.cls.lab1.validators;

import com.nova.cls.lab1.util.ByteUtils;
import com.nova.cls.lab1.util.CRC16;

public class PacketValidator {
    private static final byte PACKET_MAGIC_BYTE = 0x13;
    private static final int PACKET_MIN_BYTES = 42; // 26 total bytes for known-size fields, 16 is the minimal properly encrypted message body

    public static void validateMinPacketRequirements(byte[] packetBytes) {
        if (packetBytes.length < PACKET_MIN_BYTES) throw new IllegalArgumentException("Packet shorter than min length " + PACKET_MIN_BYTES);
        if (packetBytes[0] != PACKET_MAGIC_BYTE) throw new IllegalArgumentException("Packet does not start with magic byte 13h");
    }

    public static void validatePacketSize(byte[] packetBytes, long messageLength) throws IllegalArgumentException {
        if (messageLength + 18 != packetBytes.length) throw new IllegalArgumentException("Packet has invalid wLen field");
    }

    public static void validatePacketChecksum(byte[] packetBytes, int messageLength) throws IllegalArgumentException {
        int crcHeaders = ByteUtils.fromUnsignedShortBytes(packetBytes, 14);
        int crcHeadersComputed = CRC16.crc16(packetBytes, 0, 14);
        if (crcHeadersComputed != crcHeaders) throw new IllegalArgumentException("Packet headers CRC16 does not match expected value");

        int crcMessage = ByteUtils.fromUnsignedShortBytes(packetBytes, 16 + messageLength);
        int crcMessageComputed = CRC16.crc16(packetBytes, 16, messageLength);
        if (crcMessageComputed != crcMessage) throw new IllegalArgumentException("Packet message CRC16 does not match expected value");
    }
}
