package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ACK extends Message {
    private Short messageOpcode;
    private List<Object> optional=new LinkedList<>();

    public ACK() {
        super((byte) 10);
    }

    public ACK(short messageOpcode) {
        this();
        this.messageOpcode = messageOpcode;
    }

    public void setMessageOpcode(Short messageOpcode) {
        this.messageOpcode = messageOpcode;
    }

    public void addOptions(Object... option) {
        optional.addAll(Arrays.asList(option));
    }
    public Short getMessageOpcode() {
        return messageOpcode;
    }

    public Object[] getOptional() {
        return optional.toArray(new Object[0]);
    }

    @Override
    public String toString() {
        return "ACK "+messageOpcode+" "+Arrays.toString(optional.toArray());
    }
}
