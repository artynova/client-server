package com.nova.cls.lab1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.cls.lab1.exceptions.BadPacketException;
import com.nova.cls.lab1.util.CRC16;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class TestAll {
    private static final short MAX_UNSIGNED_BYTE = Byte.MAX_VALUE * 2 + 1;
    private static final long MAX_UNSIGNED_INT = Integer.MAX_VALUE * 2L + 1L;
    private static final BigInteger MAX_UNSIGNED_LONG = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);

    private final UserPOJO taras;
    private final Packet tarasPacket;

    public TestAll() throws JsonProcessingException {
        taras = new UserPOJO("taras_shevchenko", "kobzar123");
        ObjectMapper mapper = new ObjectMapper();
        Message message = new Message(0, 0, mapper.writeValueAsString(taras));
        tarasPacket = new Packet((byte) 0, 0, message);
    }

    @Test
    public void testUnsignedInterpretation() {
        Message message = new Message(-1, -1, "Hello, World!");
        Assert.assertEquals(MAX_UNSIGNED_INT, message.getCommandTypeUnsigned());
        Assert.assertEquals(MAX_UNSIGNED_INT, message.getUserIdUnsigned());

        Packet packet = new Packet((byte) -1, -1, message);
        Assert.assertEquals(MAX_UNSIGNED_BYTE, packet.getSourceUnsigned());
        Assert.assertEquals(MAX_UNSIGNED_LONG, packet.getPacketIdUnsigned());
    }

    @Test
    public void testCRC16() {
        byte[] dead = {(byte) 0xDE, (byte) 0xAD};
        Assert.assertEquals(0x1D98, CRC16.crc16(dead, 0, dead.length));

        byte[] deadbeef = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
        Assert.assertEquals((short) 0xE59B, CRC16.crc16(deadbeef, 0, deadbeef.length));
    }

    @Test
    public void testGoodTransmission() {
        try {
            byte[] bytes = PacketEncoder.encode(tarasPacket);

            Packet decoded = PacketDecoder.decode(bytes);

            Assert.assertEquals(tarasPacket, decoded);

            ObjectMapper mapper = new ObjectMapper();
            UserPOJO decodedBodyPojo = mapper.readValue(decoded.getMessage().getBody(), UserPOJO.class);
            Assert.assertEquals(taras, decodedBodyPojo);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e);
        }
    }

    @Test(expected = BadPacketException.class)
    public void testTooShort() throws BadPacketException {
        byte[] badBytes = new byte[41];
        PacketDecoder.decode(badBytes);
    }

    @Test(expected = BadPacketException.class)
    public void testDamaged() throws BadPacketException {
        byte[] damaged = PacketEncoder.encode(tarasPacket);
        // breaking CRC
        damaged[damaged.length - 2] = (byte) 0xDE;
        damaged[damaged.length - 1] = (byte) 0xAD;
        PacketDecoder.decode(damaged);
    }
}
