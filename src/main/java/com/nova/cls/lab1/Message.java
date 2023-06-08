package com.nova.cls.lab1;

import com.nova.cls.lab1.validators.NumberValidator;

public final class Message {
    private final long commandType;
    private final long userId;
    private final String body;

    public Message(long commandType, long userId, String body) {
        NumberValidator.validateUnsignedInt(commandType);
        NumberValidator.validateUnsignedInt(userId);
        this.commandType = commandType;
        this.userId = userId;
        this.body = body;
    }

    public long getCommandType() {
        return commandType;
    }

    public long getUserId() {
        return userId;
    }

    public String getBody() {
        return body;
    }
}
