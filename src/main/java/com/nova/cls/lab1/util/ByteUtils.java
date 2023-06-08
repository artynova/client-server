package com.nova.cls.lab1.util;

import com.nova.cls.lab1.validators.ByteValidator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Lightweight conversion utils between values and their byte representations.
 * Numeric conversions treat values in bytes as unsigned.
 * The conversions discard extra bytes in values and in byte arrays.
 */
public class ByteUtils {

    public static byte toUnsignedByte(short value) {
        return (byte) value;
    }

    public static short fromUnsignedByte(byte b) {
        return (short) (b & 0xFF);
    }


    public static byte[] toUnsignedShortBytes(int value) {
        return new byte[]{(byte) (value >> 8), (byte) value};
    }

    public static int fromUnsignedShortBytes(byte[] bytes, int offset) {
        ByteValidator.validateSubarrayDimensions(bytes.length, offset, 2);
        return ((bytes[offset] & 0xFF) << 8) | (bytes[offset + 1] & 0xFF);
    }

    public static int fromUnsignedShortBytes(byte[] bytes) {
        return fromUnsignedShortBytes(bytes, 0);
    }

    public static byte[] toUnsignedIntBytes(long value) {
        byte[] byteArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            byteArray[i] = (byte) (value >> (8 * (3 - i)));
        }
        return byteArray;
    }

    public static long fromUnsignedIntBytes(byte[] bytes, int offset) {
        ByteValidator.validateSubarrayDimensions(bytes.length, offset, 4);
        long value = 0;
        for (int i = 0; i < 4; i++) {
            value = value | ((bytes[i + offset] & 0xFFL) << (8 * (3 - i)));
        }
        return value;
    }

    public static long fromUnsignedIntBytes(byte[] bytes) {
        return fromUnsignedIntBytes(bytes, 0);
    }

    public static byte[] toUnsignedLongBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        byte[] unsignedLongBytes = new byte[8];
        if (bytes.length < 8) {
            System.arraycopy(bytes, 0, unsignedLongBytes, 8 - bytes.length, bytes.length);
        } else {
            System.arraycopy(bytes, bytes.length - 8, unsignedLongBytes, 0, 8);
        }
        return unsignedLongBytes;
    }

    public static BigInteger fromUnsignedLongBytes(byte[] bytes, int offset) {
        ByteValidator.validateSubarrayDimensions(bytes.length, offset, 8);
        return new BigInteger(1, Arrays.copyOfRange(bytes, offset, offset + 8));
    }

    public static BigInteger fromUnsignedLongBytes(byte[] bytes) {
        return fromUnsignedLongBytes(bytes, 0);
    }

    public static byte[] toBytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] toBytesEncrypted(String value) {
        byte[] plain = toBytes(value);
        Encryptor encryptor = new Encryptor();
        return encryptor.encrypt(plain);
    }

    public static String fromBytes(byte[] bytes, int offset, int length) {
        ByteValidator.validateSubarrayDimensions(bytes.length, offset, length);
        byte[] actualBytes = new byte[length];
        System.arraycopy(bytes, offset, actualBytes, 0, length);
        return new String(actualBytes, StandardCharsets.UTF_8);
    }

    public static String fromBytesEncrypted(byte[] bytes, int offset, int length) throws IllegalBlockSizeException, BadPaddingException {
        ByteValidator.validateSubarrayDimensions(bytes.length, offset, length);
        byte[] encrypted = new byte[length];
        System.arraycopy(bytes, offset, encrypted, 0, length);
        Decryptor decryptor = new Decryptor();
        byte[] plain = decryptor.decrypt(encrypted);
        return new String(plain, StandardCharsets.UTF_8);
    }
}
