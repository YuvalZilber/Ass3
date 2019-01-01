package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

@SuppressWarnings("all")
public class NOTIFICATION extends Message {

    public NOTIFICATION() {
        super((short) 9);
    }

    public void setType(Character type) {
        Character type1 = type;
    }

    public void setPostinUser(String postinUser) {
        String postinUser1 = postinUser;
    }

    public void setContent(String content) {
        String content1 = content;
    }
}
