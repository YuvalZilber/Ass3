package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.ConnectionsImpl;

import java.util.Arrays;

public class bgsProtocol implements BidiMessagingProtocol<Message> {
    private final Database db;
    private int id;

    private boolean shouldTerminate;

    bgsProtocol(Database db) {
        this.db = db;
        this.shouldTerminate = false;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.id = connectionId;
        db.initialConnections((ConnectionsImpl<Message>) connections);
    }

    @SuppressWarnings("cast")
    @Override
    public void process(Message message) {
        int[] requiredLoggedin = new int[]{3, 4, 5, 6, 7, 8};//all > 2
        short code = message.opCode;

        User user = db.getUser(id);
        if (Arrays.binarySearch(requiredLoggedin, code) > 0 & (user == null || user.getId() == -1)) {
            error(code);
            return;
        }
        switch (code) {
            case 1:
                MessageREGISTER register = (MessageREGISTER) message;
                complete(db.register(register.getUsername(), register.getPassword()), message);
                break;
            case 2:
                MessageLOGIN login = (MessageLOGIN) message;
                db.login(login.getUsername(), login.getPassword(), id);
                break;
            case 3:
                db.logout(id);
                break;
            case 4:
                MessageFOLLOW follow = (MessageFOLLOW) message;
                db.follow(id, follow.getFollow(), follow.getUserNameList());
                break;
            case 5:
                MessagePOST post = (MessagePOST) message;
                db.post(id, post);
                break;
            case 6:
                MessagePM pm = (MessagePM) message;
                complete(db.sendPM(id, pm), message);
                break;
            case 7:
                db.userList(id);
                break;
            case 8:
                MessageSTAT stat = (MessageSTAT) message;
                db.stats(id, stat.getUsername());
                break;

        }
    }

    private void complete(boolean didit, Message message) {
        short code = message.opCode;
        if (didit)
            success(code);
        else
            error(code);
    }


    private void success(short msgOpcode) {//send default ack
        ACK ack = new ACK();
        ack.setMessageOpcode(msgOpcode);
        db.send(id, ack);
    }

    private void error(short msgOpcode) {//send error
        ERROR err = new ERROR();
        err.setMessageOpcode(msgOpcode);
        db.send(id, err);
    }

    private void close() {
        shouldTerminate = true;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
