package com.nova.cls.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Decipherer {
    private static final ThreadLocal<Cipher> cipher =
        ThreadLocal.withInitial(() -> CipherUtils.createCipher(Cipher.DECRYPT_MODE)); // making Decipherer thread-safe

    public byte[] decipher(byte[] bytes, int offset, int length) throws IllegalBlockSizeException, BadPaddingException {
        try {
            return cipher.get().doFinal(bytes, offset, length);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            CipherUtils.resetCipher(cipher.get(), Cipher.DECRYPT_MODE); // reset when errored
            throw e;
        }
    }
}
