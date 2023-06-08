package com.nova.cls.lab1.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Decryptor {
    private final Cipher cipher;

    public Decryptor() {
        this.cipher = CipherUtils.createCipher(Cipher.DECRYPT_MODE);
    }

    public byte[] decrypt(byte[] bytes, int offset, int length) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(bytes, offset, length);
    }

    public byte[] decrypt(byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
        return decrypt(bytes, 0, bytes.length);
    }
}
