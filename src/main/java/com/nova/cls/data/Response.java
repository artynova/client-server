package com.nova.cls.data;

public enum Response {
    OK,
    BAD_REQUEST,
    SERVER_ERROR,
    RETRANSMIT_REQUEST; // for UDP

    public static Response get(int i) {
        return values()[i];
    }
}
