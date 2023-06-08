package com.nova.cls.lab1.validators;

public class ByteValidator {
    public static void validateSubarrayDimensions(int length, int offset, int minLength) throws IllegalArgumentException {
        if (offset < 0) throw new IllegalArgumentException("Illegal offset " + offset);
        if (minLength < 0) throw new IllegalArgumentException("Illegal min length " + minLength);
        if (length - offset < minLength) throw new IllegalArgumentException("Array is shorter than min length " + minLength);
    }
}
