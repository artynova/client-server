package com.nova.cls;

import com.nova.cls.network.packets.Message;
import com.nova.cls.network.packets.Packet;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class TestPackets {
    private static final short MAX_UNSIGNED_BYTE = Byte.MAX_VALUE * 2 + 1;
    private static final long MAX_UNSIGNED_INT = Integer.MAX_VALUE * 2L + 1L;
    private static final BigInteger MAX_UNSIGNED_LONG =
        BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);

    @Test
    public void testUnsignedInterpretation() {
        Message message = new Message(-1, -1, "Hello, World!");
        assertEquals(MAX_UNSIGNED_INT, message.getMessageTypeUnsigned());
        assertEquals(MAX_UNSIGNED_INT, message.getUserIdUnsigned());

        Packet packet = new Packet((byte) -1, -1, message);
        assertEquals(MAX_UNSIGNED_BYTE, packet.getSourceUnsigned());
        assertEquals(MAX_UNSIGNED_LONG, packet.getPacketIdUnsigned());
    }
}
