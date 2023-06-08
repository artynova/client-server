package com.nova.cls.lab1.util;

import javax.crypto.Cipher;

public class Encryptor {
    private final Cipher cipher;

    public Encryptor() {
        this.cipher = CipherUtils.createCipher(Cipher.ENCRYPT_MODE);
    }

    public byte[] encrypt(byte[] bytes, int offset, int length) {
        try {
            return cipher.doFinal(bytes, offset, length);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1); // With proper settings, encryption should never fail, so this is non-recoverable
        }
        return null;
    }

    public byte[] encrypt(byte[] bytes) {
        return encrypt(bytes, 0, bytes.length);
    }
}
