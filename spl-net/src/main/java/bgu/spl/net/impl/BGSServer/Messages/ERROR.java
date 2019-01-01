package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class ERROR extends Message {
    private Short messageOpcode;

    public ERROR() {
        super((byte) 11);
    }

    public ERROR(short messageOpcode) {
        this();
        this.messageOpcode = messageOpcode;
    }

    public void setMessageOpcode(Short messageOpcode) {
        this.messageOpcode = messageOpcode;
    }
}
