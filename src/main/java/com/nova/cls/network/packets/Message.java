package com.nova.cls.network.packets;

import com.nova.cls.data.Command;
import com.nova.cls.data.Response;

import java.util.Objects;

// numbers are interpreted as unsigned
public final class Message {
    public static final int BYTES_WITHOUT_BODY = 8;

    private final int messageType; // changed from command type to message type to reflect the fact that it is used by both client and server, with different type classification
    private final int userId;
    private final String body;

    public Message(int messageType, int userId, Object body) {
        this.messageType = messageType;
        this.userId = userId;
        this.body = body.toString();
    }

    public Message(Command command, int userId, Object body) {
        this(command.ordinal(), userId, body);
    }

    public Message(Response response, int userId, Object body) {
        this(response.ordinal(), userId, body);
    }

    public int getMessageType() {
        return messageType;
    }

    public long getMessageTypeUnsigned() {
        return Integer.toUnsignedLong(messageType);
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
        return getMessageType() == message.getMessageType() && getUserId() == message.getUserId() && getBody().equals(message.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageType(), getUserId(), getBody());
    }

    @Override
    public String toString() {
        return "Message { " +
                "commandType = " + getMessageTypeUnsigned() +
                ", userId = " + getUserIdUnsigned() +
                ", body = '" + getBody() + "' }";
    }
}
