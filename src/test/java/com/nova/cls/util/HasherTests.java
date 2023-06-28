package com.nova.cls.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HasherTests {
    @Test
    public void hashPassword() {
        Hasher hasher = new Hasher();
        assertEquals("279dc449bba966c322f308f955f5b3e6", hasher.hash("artynova_password"));
        assertEquals("10acee29dd20946a2fac67cca715bfa5", hasher.hash("trosha_b_password"));
    }
}