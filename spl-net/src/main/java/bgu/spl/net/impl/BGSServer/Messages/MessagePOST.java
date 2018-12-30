package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class MessagePOST extends Message implements ContentHolder {
    private String content;
    public MessagePOST() {
        super((short) 5);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return ("POST " + content);
    }
}

