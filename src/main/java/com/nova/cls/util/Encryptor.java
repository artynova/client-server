package com.nova.cls.util;

import javax.crypto.Cipher;

public class Encryptor {
    private static final ThreadLocal<Cipher> cipher =
        ThreadLocal.withInitial(() -> CryptoUtils.createCipher(Cipher.ENCRYPT_MODE)); // making Encryptor thread-safe

    public byte[] cipher(byte[] bytes, int offset, int length) {
        try {
            return cipher.get().doFinal(bytes, offset, length);
        } catch (Exception e) {
            CryptoUtils.resetCipher(cipher.get(), Cipher.ENCRYPT_MODE);
            // With proper settings, encryption should never fail, so this is non-recoverable
            throw new SecurityException(e.getMessage(), e);
        }
    }

    public byte[] cipher(byte[] bytes) {
        return cipher(bytes, 0, bytes.length);
    }
}
