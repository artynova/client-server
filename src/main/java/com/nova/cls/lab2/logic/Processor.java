package com.nova.cls.lab2.logic;

import com.nova.cls.lab2.packets.Message;
import com.nova.cls.lab2.packets.Packet;

public class Processor {
    // made to return the response packet instead of passing it along, to centralize chaining stages of packet processing within the handler
    public Packet process(Packet request) {
        Message requestMessage = request.getMessage();
        Message responseMessage = new Message(requestMessage.getCommandType(), requestMessage.getUserId(), "OK");
        return new Packet(request.getSource(), request.getPacketId(), responseMessage);
    }
}
