package com.nova.cls.network.packets;

import com.nova.cls.util.CRC16;
import com.nova.cls.util.Cipherer;

import java.nio.ByteBuffer;

public class Encryptor {
    private static final Cipherer CIPHERER = new Cipherer(); // thread-safe

    public byte[] encrypt(Packet packet) {
        byte[] bodyPlain = packet.getMessage().getBody().getBytes();
        byte[] bodyEncrypted = CIPHERER.cipher(bodyPlain, 0, bodyPlain.length);

        int messageLength = Message.BYTES_WITHOUT_BODY + bodyEncrypted.length;
        int packetLength = Packet.BYTES_WITHOUT_MESSAGE + messageLength;
        ByteBuffer buffer = ByteBuffer.allocate(packetLength);

        // bMagic
        buffer.put(0, Packet.MAGIC_BYTE);
        // bSrc
        buffer.put(1, packet.getSource());
        // bPktId
        buffer.putLong(Packet.PACKET_ID_OFFSET, packet.getPacketId());
        // wLen
        buffer.putInt(Packet.MESSAGE_LENGTH_OFFSET, messageLength);
        // wCrc16
        buffer.putShort(14, CRC16.crc16(buffer.array(), 0, 14));

        // bMsq
        // cType
        buffer.putInt(16, packet.getMessage().getMessageType());
        // bUserId
        buffer.putInt(20, packet.getMessage().getUserId());
        // message
        buffer.position(24);
        buffer.put(bodyEncrypted);

        // wCrc16
        buffer.putShort(packetLength - 2, CRC16.crc16(buffer.array(), 16, messageLength));

        return buffer.array();
    }
}
