package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class MessageSTAT extends Message {
    private String username;

    public MessageSTAT() {
        super((short) 8);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "STAT " + username;
    }
}
