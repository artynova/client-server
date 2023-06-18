package com.nova.cls.data;

import com.nova.cls.network.packets.Message;
import com.nova.cls.network.packets.Packet;

public class Processor {
    // made to return the response packet instead of passing it along, to centralize chaining stages of packet processing within the handler
    public Packet process(Packet request) {
        Message requestMessage = request.getMessage();
        Message responseMessage = new Message(requestMessage.getCommandType(), requestMessage.getUserId(), "OK");
        return new Packet(request.getSource(), request.getPacketId(), responseMessage);
    }
}
