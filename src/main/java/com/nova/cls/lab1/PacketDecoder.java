package com.nova.cls.lab1;

import com.nova.cls.lab1.exceptions.BadPacketException;
import com.nova.cls.lab1.util.ByteUtils;
import com.nova.cls.lab1.util.Decryptor;
import com.nova.cls.lab1.validators.PacketValidator;

import java.math.BigInteger;

public class PacketDecoder {
    private static final Decryptor decryptor = new Decryptor();

    public static Packet decode(byte[] bytes) throws BadPacketException {
        try {
            PacketValidator.validateMinPacketRequirements(bytes);

            long messageLengthLong = ByteUtils.fromUnsignedIntBytes(bytes, 10);
            PacketValidator.validatePacketSize(bytes, messageLengthLong);
            int messageLength = (int) messageLengthLong; // has to fit in a signed int because it is length of a byte subarray
            PacketValidator.validatePacketChecksum(bytes, messageLength);

            long commandType = ByteUtils.fromUnsignedIntBytes(bytes, 16);
            long userId = ByteUtils.fromUnsignedIntBytes(bytes, 20);
            String body = ByteUtils.fromBytesEncrypted(bytes, 24, messageLength - 8);

            Message message = new Message(commandType, userId, body);
            short source = ByteUtils.fromUnsignedByte(bytes[1]);
            BigInteger packetId = ByteUtils.fromUnsignedLongBytes(bytes, 2);

            return new Packet(source, packetId, message);
        } catch (Exception e) {
            throw new BadPacketException(e.getMessage(), e); // All exceptions are wrapped in semantic bad packet exceptions
        }
    }
}
