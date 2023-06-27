package com.nova.cls;

import com.nova.cls.util.PasswordHasher;
import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordHasherTest {
    @Test
    public void testHashPassword() {
        PasswordHasher hasher = new PasswordHasher();
        assertEquals("279dc449bba966c322f308f955f5b3e6", hasher.hashPassword("artynova_password"));
        assertEquals("10acee29dd20946a2fac67cca715bfa5", hasher.hashPassword("trosha_b_password"));
    }
}