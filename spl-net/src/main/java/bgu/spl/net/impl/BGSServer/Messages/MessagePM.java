package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class MessagePM extends Message implements ContentHolder {
    private String username;
    private String Content;
    private String sender;
    public MessagePM() {
        super((short) 6);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getSender() { return sender; }

    public void setSender(String sender) { this.sender = sender; }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return Content;
    }
}
