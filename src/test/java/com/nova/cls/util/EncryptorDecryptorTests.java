package com.nova.cls.util;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class EncryptorDecryptorTests {
    @Test
    public void cipher() throws Exception {
        Encryptor encryptor = new Encryptor();
        Decryptor decryptor = new Decryptor();

        assertEquals("Hello, World!", new String(decryptor.decipher(encryptor.cipher("Hello, World!".getBytes()))));
    }
}
