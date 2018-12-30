package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.impl.BGSServer.Message;

public class MessageREGISTER extends Message {
    private String username;
    private String password;

    public MessageREGISTER() {
        super((short) 1);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "REGISTER " + username + " " + password;
    }
}

