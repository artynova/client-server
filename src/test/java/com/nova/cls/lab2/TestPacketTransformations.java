package com.nova.cls.lab2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.cls.lab2.packets.*;
import com.nova.cls.lab2.util.CRC16;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPacketTransformations {

    private final Decryptor decryptor = new Decryptor();
    private final Encryptor encryptor = new Encryptor();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Group meat = new Group("Meat", "Bad for the environment, hard to give up");
    private Packet meatPacket;

    @Before
    public void setUp() throws Exception {
        meatPacket = new Packet((byte) 0, 0, new Message(0, 0, mapper.writeValueAsString(meat)));
    }

    @Test
    public void testCRC16() {
        byte[] dead = {(byte) 0xDE, (byte) 0xAD};
        assertEquals((short) 0x1D98, CRC16.crc16(dead, 0, dead.length));

        byte[] deadbeef = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
        assertEquals((short) 0xE59B, CRC16.crc16(deadbeef, 0, deadbeef.length));
    }

    @Test
    public void testGoodTransmission() {
        try {
            byte[] bytes = encryptor.encrypt(meatPacket);

            Packet decoded = decryptor.decrypt(bytes);

            assertEquals(meatPacket, decoded);

            ObjectMapper mapper = new ObjectMapper();
            Group decodedBodyPojo = mapper.readValue(decoded.getMessage().getBody(), Group.class);
            assertEquals(meat, decodedBodyPojo);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception " + e);
        }
    }

    @Test(expected = BadPacketException.class)
    public void testTooShort() throws BadPacketException {
        byte[] badBytes = new byte[41];
        decryptor.decrypt(badBytes);
    }

    @Test(expected = BadPacketException.class)
    public void testDamaged() throws BadPacketException {
        Decryptor decryptor = new Decryptor();
        byte[] damaged = encryptor.encrypt(meatPacket);
        // breaking CRC
        damaged[damaged.length - 2] = (byte) 0xDE;
        damaged[damaged.length - 1] = (byte) 0xAD;
        decryptor.decrypt(damaged);
    }
}
