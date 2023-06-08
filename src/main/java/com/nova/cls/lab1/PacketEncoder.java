package com.nova.cls.lab1;

import com.nova.cls.lab1.util.ByteUtils;
import com.nova.cls.lab1.util.CRC16;
import com.nova.cls.lab1.util.Encryptor;

import java.nio.ByteBuffer;

public class PacketEncoder {
    private static final byte MAGIC_BYTE = 0x13;
    private static final Encryptor encryptor = new Encryptor();

    public static byte[] encryptPacket(Packet packet) {
        //Transforming and encrypting body
        byte[] body = ByteUtils.toBytesEncrypted(packet.getMessage().getBody());

        // Calculate the length of the message body
        int messageLength = 8 + body.length;

        byte[] temp;
        // Calculate the total length of the packet
        int packetLength = 18 + messageLength;

        ByteBuffer buffer = ByteBuffer.allocate(packetLength);

        // bMagic
        buffer.put(MAGIC_BYTE);

        // bSrc
        buffer.position(1);
        buffer.put(ByteUtils.toUnsignedByte(packet.getSource()));

        // bPktId
        buffer.position(2);
        temp = ByteUtils.toUnsignedLongBytes(packet.getPacketId());
        buffer.put(temp);

        // wLen
        buffer.position(10);
        temp = ByteUtils.toUnsignedIntBytes(messageLength);
        buffer.put(temp);

        // wCrc16
        buffer.position(14);
        temp = ByteUtils.toUnsignedShortBytes(CRC16.crc16(buffer.array(), 0, 14));
        buffer.put(temp);

        // bMsq
        buffer.position(16);
        temp = ByteUtils.toUnsignedIntBytes(packet.getMessage().getCommandType());
        buffer.put(temp);
        buffer.position(20);
        temp = ByteUtils.toUnsignedIntBytes(packet.getMessage().getUserId());
        buffer.put(temp);
        //Use prev transformed and encrypted body;
        buffer.position(24);
        temp = body;
        buffer.put(body);

        // wCrc16
        buffer.position(packetLength-2);
        temp = ByteUtils.toUnsignedShortBytes(CRC16.crc16(buffer.array(), 16, messageLength));
        buffer.put(temp);

        return buffer.array();
    }
}
