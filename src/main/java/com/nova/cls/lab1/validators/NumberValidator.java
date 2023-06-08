package com.nova.cls.lab1.validators;

import java.math.BigInteger;

public class NumberValidator {
    private static final short MAX_UNSIGNED_BYTE = Byte.MAX_VALUE * 2 + 1;
    private static final int MAX_UNSIGNED_SHORT = Short.MAX_VALUE * 2 + 1;
    private static final long MAX_UNSIGNED_INT = Integer.MAX_VALUE * 2L + 1L;
    private static final BigInteger MAX_UNSIGNED_LONG = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);

    public static void validateUnsignedByte(short value) throws IllegalArgumentException {
        if (value < 0) throw new IllegalArgumentException("Short value representing unsigned byte is negative, " + value);
        if (value > MAX_UNSIGNED_BYTE) throw new IllegalArgumentException("Short value representing unsigned byte is too large, " + value);
    }


    public static void validateUnsignedShort(int value) throws IllegalArgumentException {
        if (value < 0) throw new IllegalArgumentException("Int value representing unsigned short is negative, " + value);
        if (value > MAX_UNSIGNED_SHORT) throw new IllegalArgumentException("Int value representing unsigned short is too large, " + value);
    }

    public static void validateUnsignedInt(long value) throws IllegalArgumentException {
        if (value < 0) throw new IllegalArgumentException("Long value representing unsigned int is negative, " + value);
        if (value > MAX_UNSIGNED_INT) throw new IllegalArgumentException("Long value representing unsigned int is too large, " + value);
    }

    public static void validateUnsignedLong(BigInteger value) throws IllegalArgumentException {
        if (value.compareTo(BigInteger.ZERO) < 0) throw new IllegalArgumentException("BigInteger value representing unsigned long is negative, " + value);
        if (value.compareTo(MAX_UNSIGNED_LONG) > 0) throw new IllegalArgumentException("BigInteger value representing unsigned long is too large, " + value);
    }
}
