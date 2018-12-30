package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class MessageLOGOUT extends Message {
    public MessageLOGOUT() {
        super((short) 3);
    }

    @Override
    public String toString() {
        return "LOGOUT";
    }
}
