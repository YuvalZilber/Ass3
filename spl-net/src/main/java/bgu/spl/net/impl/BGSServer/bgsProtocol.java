package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Messages.*;

public class bgsProtocol implements BidiMessagingProtocol<Message> {
    private DataBase db;
    private int id;
    private Connections<Message> clients;

    private boolean shouldTerminate;

    public bgsProtocol(DataBase db) {
        this.db = db;
        this.shouldTerminate = false;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.id = connectionId;
        this.clients = connections;
    }

    @SuppressWarnings("cast")
    @Override
    public void process(Message message) {
        short code = message.opCode;
        if (code > 2 && !db.isUserLoggedIn(id))
            error(code);
        switch (code) {
            case 1:
                MessageREGISTER register = (MessageREGISTER) message;
                complete(db.register(register.getUsername()), message);
            case 2:
                MessageLOGIN login = (MessageLOGIN) message;
                complete(db.login(login.getUsername(), id), message);
            case 3:
                complete(db.logout(id), message);
            case 4:
                MessageFOLLOW follow = (MessageFOLLOW) message;
                db.follow(id, follow.getFollow(), follow.getUserNameList());
            case 5:
                MessagePOST post = (MessagePOST) message;
                complete(db.post(id, post), message);
            case 6:
                MessagePM pm = (MessagePM) message;
                complete(db.sendPM(id, pm), message);
            case 7:
                db.userList(id);
            case 8:
                MessageSTAT stat = (MessageSTAT) message;
                db.stats(id, stat.getUsername());

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
