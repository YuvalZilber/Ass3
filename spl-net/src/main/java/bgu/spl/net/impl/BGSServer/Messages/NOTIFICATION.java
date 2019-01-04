package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

@SuppressWarnings("all")
public class NOTIFICATION extends Message {

    public NOTIFICATION() {
        super((short) 9);
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public void setPostinUser(String postinUser) {
        this.postingUser = postinUser;
    }

    public void setContent(String content) {
        this.content = content;
    }

    Byte type;
    String postingUser;
String content;

    @Override
    public String toString() {
        return "NOTIFOCATION: "+(type==0?"PM":"POST")+" "+postingUser+" "+content;
    }
}
