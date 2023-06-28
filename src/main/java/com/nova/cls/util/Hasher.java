package com.nova.cls.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    private static final ThreadLocal<MessageDigest> mdLocal = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // should not happen because MD5 does exist
            e.printStackTrace();
            throw new SecurityException("Unexpectedly, MD5 algorithm was not found", e);
        }
    });

    /**
     * @param string The string.
     * @return Hash produced by MD5.
     */
    public String hash(String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes = mdLocal.get().digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            sb.append(Integer.toHexString((hashByte & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }
}

