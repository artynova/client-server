package com.nova.cls.lab2.network.packets;

import java.util.Objects;

// numbers are interpreted as unsigned
public final class Message {
    public static final int BYTES_WITHOUT_BODY = 8;

    private final int commandType;
    private final int userId;
    private final String body;

    public Message(int commandType, int userId, String body) {
        this.commandType = commandType;
        this.userId = userId;
        this.body = body;
    }

    public int getCommandType() {
        return commandType;
    }

    public long getCommandTypeUnsigned() {
        return Integer.toUnsignedLong(commandType);
    }

    public int getUserId() {
        return userId;
    }

    public long getUserIdUnsigned() {
        return Integer.toUnsignedLong(userId);
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return getCommandType() == message.getCommandType() && getUserId() == message.getUserId() && getBody().equals(message.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getUserId(), getBody());
    }

    @Override
    public String toString() {
        return "Message { " +
                "commandType = " + getCommandTypeUnsigned() +
                ", userId = " + getUserIdUnsigned() +
                ", body = '" + getBody() + "' }";
    }
}
