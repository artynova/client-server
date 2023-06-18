package com.nova.cls.network.packets;

import com.nova.cls.util.Decipherer;
import com.nova.cls.util.PacketValidator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Decryptor {
    private static final Decipherer DECIPHERER = new Decipherer(); // thread-safe

    // made to return the request packet instead of passing it along, to centralize chaining stages of packet processing within the handler
    public Packet decrypt(byte[] bytes) throws BadPacketException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            PacketValidator.validateMinPacketRequirements(buffer);

            // wLen
            int messageLength = buffer.getInt(Packet.MESSAGE_LENGTH_OFFSET);

            PacketValidator.validatePacket(buffer, messageLength);

            // cType
            int messageType = buffer.getInt(16);
            // bUserId
            int userId = buffer.getInt(20);
            // message
            String body = new String(DECIPHERER.decipher(bytes, 24, messageLength - Message.BYTES_WITHOUT_BODY), StandardCharsets.UTF_8);

            // bMsq
            Message message = new Message(messageType, userId, body);
            // bSrc
            byte source = buffer.get(1);
            // bPktId
            long packetId = buffer.getLong(Packet.PACKET_ID_OFFSET);

            return new Packet(source, packetId, message);
        } catch (Exception e) {
            throw new BadPacketException(e.getMessage(), e); // All exceptions are wrapped in semantic bad packet exceptions
        }
    }
}
