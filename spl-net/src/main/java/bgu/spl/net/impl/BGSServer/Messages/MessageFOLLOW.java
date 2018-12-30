package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

import java.util.Arrays;

public class MessageFOLLOW extends Message {
    private Byte follow;
    private Short numOfUsers;
    private String[] userNameList;

    public MessageFOLLOW() {
        super((byte) 4);
    }

    public void setFollow(Byte follow) {
        this.follow = follow;
    }

    public void setNumOfUsers(Short numOfUsers) {
        this.numOfUsers = numOfUsers;
    }

    public void setUserNameList(String[] userNameList) {
        this.userNameList = userNameList;
    }

    public Byte getFollow() {
        return follow;
    }

    public Short getNumOfUsers() {
        return numOfUsers;
    }

    public String[] getUserNameList() {
        return userNameList;
    }

    @Override
    public String toString() {
        return "FOLLOW " + follow.toString() + " " + numOfUsers + " " + Arrays.toString(userNameList);
    }
}
