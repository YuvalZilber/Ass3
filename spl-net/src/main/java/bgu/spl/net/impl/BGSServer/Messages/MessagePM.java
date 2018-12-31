package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class MessagePM extends Message implements ContentHolder {
    private String username;
    private String Content;
    public MessagePM() {
        super((short) 6);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return Content;
    }

    @Override
    public String toString() {
        return "PM "+username+" "+Content;
    }
}
