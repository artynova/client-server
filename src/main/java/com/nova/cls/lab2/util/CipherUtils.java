package com.nova.cls.lab2.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class CipherUtils {
    private static final String ALGORITHM = "AES";
    private static final String MODE = "ECB/PKCS5Padding";

    private static final Key secretKey = new SecretKeySpec(new byte[]{
            (byte) 0xDE,
            (byte) 0xAD,
            (byte) 0xCA,
            (byte) 0xFE,
            (byte) 0xDE,
            (byte) 0xAD,
            (byte) 0xBE,
            (byte) 0xEF,
            (byte) 0xDE,
            (byte) 0xAD,
            (byte) 0xCA,
            (byte) 0xFE,
            (byte) 0xDE,
            (byte) 0xAD,
            (byte) 0xBE,
            (byte) 0xEF,

    }, ALGORITHM);

    public static Cipher createCipher(int mode) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + MODE);
            cipher.init(mode, secretKey);
            return cipher;
        } catch (Exception e) {
            throw new CipherException(e.getMessage(), e); // With proper settings, cipher initialization should never fail, so this is likely non-recoverable
        }
    }

    public static void resetCipher(Cipher cipher, int mode) {
        try {
            cipher.init(mode, secretKey);
        } catch (Exception e) {
            throw new CipherException(e.getMessage(), e); // With proper settings, cipher initialization should never fail, so this is likely non-recoverable
        }
    }
}