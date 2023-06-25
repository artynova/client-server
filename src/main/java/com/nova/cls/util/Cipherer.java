package com.nova.cls.util;

import javax.crypto.Cipher;

public class Cipherer {
    private static final ThreadLocal<Cipher> cipher =
        ThreadLocal.withInitial(() -> CipherUtils.createCipher(Cipher.ENCRYPT_MODE)); // making Cipherer thread-safe

    public byte[] cipher(byte[] bytes, int offset, int length) {
        try {
            return cipher.get().doFinal(bytes, offset, length);
        } catch (Exception e) {
            CipherUtils.resetCipher(cipher.get(), Cipher.ENCRYPT_MODE);
            throw new CipherException(e.getMessage(),
                e); // With proper settings, encryption should never fail, so this is non-recoverable
        }
    }
}
