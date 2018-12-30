package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class NOTIFICATION extends Message {
    private Character type;
    private String postinUser;
    private String content;

    public NOTIFICATION() {
        super((short) 9);
    }

    public void setType(Character type) {
        this.type = type;
    }

    public void setPostinUser(String postinUser) {
        this.postinUser = postinUser;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
