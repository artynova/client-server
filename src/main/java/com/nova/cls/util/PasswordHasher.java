package com.nova.cls.util;

import com.nova.cls.network.ClientFailureException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    private final MessageDigest md;
    public PasswordHasher() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // should not happen because MD5 does definitely exist
            throw new ClientFailureException("Unexpectedly, MD5 algorithm was not found", e);
        }
    }

    /**
     * @param password Password.
     * @return Password hash produced by MD5.
     */
    public String hashPassword(String password) {
        byte[] passwordBytes = password.getBytes();
        byte[] hashBytes = md.digest(passwordBytes);
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            sb.append(Integer.toHexString((hashByte & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }
}

