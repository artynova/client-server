package com.nova.cls.network;

public class Response {
    private final int code;
    private final String body;

    public Response(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public Response(int code) {
        this(code, "");
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
