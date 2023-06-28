package com.nova.cls.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Decryptor {
    private static final ThreadLocal<Cipher> cipher =
        ThreadLocal.withInitial(() -> CryptoUtils.createCipher(Cipher.DECRYPT_MODE)); // making Decryptor thread-safe

    public byte[] decipher(byte[] bytes, int offset, int length) throws IllegalBlockSizeException, BadPaddingException {
        try {
            return cipher.get().doFinal(bytes, offset, length);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            CryptoUtils.resetCipher(cipher.get(), Cipher.DECRYPT_MODE); // reset when errored
            throw e;
        }
    }

    public byte[] decipher(byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
        return decipher(bytes, 0, bytes.length);
    }
}
