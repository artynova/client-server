package com.nova.cls.lab1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.cls.lab1.util.ByteUtils;
import com.nova.cls.lab1.util.CRC16;
import com.nova.cls.lab1.util.Encryptor;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class TestAll {
    @Test
    public void testEncoder() {
        Packet packet = new Packet((short) 10, BigInteger.valueOf(10), new Message(10, 10, "{\"body\": \"10\"}"));
        byte[] encryptedPacket = PacketEncoder.encryptPacket(packet);

        Assert.assertNotNull(encryptedPacket);

        byte[] body = ByteUtils.toBytesEncrypted(packet.getMessage().getBody());
        int expectedPacketLength = 26 + body.length;
        Assert.assertEquals(expectedPacketLength, encryptedPacket.length);

        byte expectedMagicByte = 0x13;
        Assert.assertEquals(expectedMagicByte, encryptedPacket[0]);

        short expectedSource = packet.getSource();
        short actualSource = ByteUtils.fromUnsignedByte(encryptedPacket[1]);
        Assert.assertEquals(expectedSource, actualSource);

        BigInteger expectedPktId = packet.getPacketId();
        byte[] pktIdTest = new byte[8];
        System.arraycopy(encryptedPacket, 2, pktIdTest, 0, 8);
        BigInteger actualPktId = ByteUtils.fromUnsignedLongBytes(pktIdTest);
        Assert.assertEquals(expectedPktId, actualPktId);

        long expectedCommandType = packet.getMessage().getCommandType();
        byte[] commandTypeTest = new byte[4];
        System.arraycopy(encryptedPacket, 16, commandTypeTest, 0, 4);
        long actualCommandType = ByteUtils.fromUnsignedIntBytes(commandTypeTest);
        Assert.assertEquals(expectedCommandType, actualCommandType);

        long expectedUserId = packet.getMessage().getCommandType();
        byte[] userIdTest = new byte[4];
        System.arraycopy(encryptedPacket, 20, userIdTest, 0, 4);
        long actualUserId = ByteUtils.fromUnsignedIntBytes(userIdTest);
        Assert.assertEquals(expectedUserId, actualUserId);

        int crc16Header1 = CRC16.crc16(encryptedPacket, 0, 14);
        byte[] crc16Test1 = new byte[2];
        System.arraycopy(encryptedPacket, 14, crc16Test1, 0, 2);
        int expectedCrc16Header1 = ByteUtils.fromUnsignedShortBytes(crc16Test1);
        Assert.assertEquals(expectedCrc16Header1, crc16Header1);

        int crc16Header2 = CRC16.crc16(encryptedPacket, 16, expectedPacketLength - 18);
        byte[] crc16Test2 = new byte[2];
        System.arraycopy(encryptedPacket, expectedPacketLength - 2, crc16Test2, 0, 2);
        int expectedCrc16Header2 = ByteUtils.fromUnsignedShortBytes(crc16Test2);
        Assert.assertEquals(expectedCrc16Header2, crc16Header2);
    }

    @Test
    public void testUnsignedByte() {
        byte b = ByteUtils.toUnsignedByte((short) 0xDE);
        Assert.assertEquals((byte) 0xDE, b);

        short number = ByteUtils.fromUnsignedByte(b);
        Assert.assertEquals((short) 0xDE, number);
    }

    @Test
    public void testUnsignedShortBytes() {
        byte[] sequence = ByteUtils.toUnsignedShortBytes(0xCAFE);
        Assert.assertEquals((byte) 0xCA, sequence[0]);
        Assert.assertEquals((byte) 0xFE, sequence[1]);
        Assert.assertEquals(2, sequence.length);

        long number = ByteUtils.fromUnsignedShortBytes(sequence);
        Assert.assertEquals(0xCAFE, number);
    }

    @Test
    public void testUnsignedIntBytes() {
        byte[] sequence = ByteUtils.toUnsignedIntBytes(0xDEADBEEFL);
        Assert.assertEquals((byte) 0xDE, sequence[0]);
        Assert.assertEquals((byte) 0xAD, sequence[1]);
        Assert.assertEquals((byte) 0xBE, sequence[2]);
        Assert.assertEquals((byte) 0xEF, sequence[3]);
        Assert.assertEquals(4, sequence.length);

        long number = ByteUtils.fromUnsignedIntBytes(sequence);
        Assert.assertEquals(0xDEADBEEFL, number);
    }

    @Test
    public void testUnsignedLongBytes() {
        BigInteger original = new BigInteger("DEADCAFEDEADBEEF", 16);

        byte[] sequence = ByteUtils.toUnsignedLongBytes(original);
        Assert.assertEquals((byte) 0xDE, sequence[0]);
        Assert.assertEquals((byte) 0xAD, sequence[1]);
        Assert.assertEquals((byte) 0xCA, sequence[2]);
        Assert.assertEquals((byte) 0xFE, sequence[3]);
        Assert.assertEquals((byte) 0xDE, sequence[4]);
        Assert.assertEquals((byte) 0xAD, sequence[5]);
        Assert.assertEquals((byte) 0xBE, sequence[6]);
        Assert.assertEquals((byte) 0xEF, sequence[7]);
        Assert.assertEquals(8, sequence.length);

        BigInteger number = ByteUtils.fromUnsignedLongBytes(sequence);
        Assert.assertEquals(original, number);
    }

    @Test
    public void testCRC16() {
        byte[] dead = {(byte) 0xDE, (byte) 0xAD};
        Assert.assertEquals(0x1D98, CRC16.crc16(dead));

        byte[] deadbeef = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
        Assert.assertEquals(0xE59B, CRC16.crc16(deadbeef));
    }

    @Test
    public void testEnd2EndPacket() {
        try {
            UserPOJO user = new UserPOJO("taras_shevchenko", "kobzar123");
            ObjectMapper mapper = new ObjectMapper();
            Message message = new Message(0, 0, mapper.writeValueAsString(user));
            Packet packet = new Packet((short) 0, BigInteger.ZERO, message);
            byte[] bytes = PacketEncoder.encryptPacket(packet);
            Packet decoded = PacketDecoder.decode(bytes);

            Assert.assertEquals(packet.getSource(), decoded.getSource());
            Assert.assertEquals(packet.getPacketId(), decoded.getPacketId());

            Message decodedMessage = decoded.getMessage();
            Assert.assertEquals(message.getCommandType(), decodedMessage.getCommandType());
            Assert.assertEquals(message.getUserId(), decodedMessage.getUserId());

            UserPOJO decodedBodyPojo = mapper.readValue(decodedMessage.getBody(), UserPOJO.class);
            Assert.assertEquals(user.getLogin(), decodedBodyPojo.getLogin());
            Assert.assertEquals(user.getPassword(), decodedBodyPojo.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e);
        }
    }
}
