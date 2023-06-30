package com.nova.cls.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String MODE = "ECB/PKCS5Padding";

    private static final Key secretKey =
        new SecretKeySpec(Base64.getDecoder().decode("8GCqgMtGocuefwAw3BsQoMV1iM6dzEjRerYzb/a4ICk="), ALGORITHM);

    public static Cipher createCipher(int mode) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + MODE);
            cipher.init(mode, secretKey);
            return cipher;
        } catch (Exception e) {
            throw new SecurityException(e.getMessage(),
                e); // With proper settings, cipher initialization should never fail, so this is likely non-recoverable
        }
    }

    public static void resetCipher(Cipher cipher, int mode) {
        try {
            cipher.init(mode, secretKey);
        } catch (Exception e) {
            throw new SecurityException(e.getMessage(),
                e); // With proper settings, cipher initialization should never fail, so this is likely non-recoverable
        }
    }
}