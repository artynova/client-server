package com.nova.cls.lab1;

import com.nova.cls.lab1.exceptions.BadPacketException;
import com.nova.cls.lab1.util.Decryptor;
import com.nova.cls.lab1.util.PacketValidator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketDecoder {
    private static final Decryptor decryptor = new Decryptor();

    public static Packet decode(byte[] bytes) throws BadPacketException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            PacketValidator.validateMinPacketRequirements(buffer);

            // wLen
            int messageLength = buffer.getInt(10);

            PacketValidator.validatePacket(buffer, messageLength);

            // cType
            int commandType = buffer.getInt(16);
            // bUserId
            int userId = buffer.getInt(20);
            // message
            String body = new String(decryptor.decrypt(bytes, 24, messageLength - Message.BYTES_WITHOUT_BODY), StandardCharsets.UTF_8);

            // bMsq
            Message message = new Message(commandType, userId, body);
            // bSrc
            byte source = buffer.get(1);
            // bPktId
            long packetId = buffer.getLong(2);

            return new Packet(source, packetId, message);
        } catch (Exception e) {
            throw new BadPacketException(e.getMessage(), e); // All exceptions are wrapped in semantic bad packet exceptions
        }
    }
}
