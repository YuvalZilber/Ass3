package bgu.spl.net.impl.rci;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.io.Serializable;

public class RemoteCommandInvocationProtocol<T> implements BidiMessagingProtocol<Serializable> {

    private T arg;
    private int id;
    private Connections<Serializable> connections;

    public RemoteCommandInvocationProtocol(T arg) {
        this.arg = arg;
    }

    @Override
    public void start(int connectionId, Connections<Serializable> connections) {
        this.id = connectionId;
        this.connections = connections;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Serializable msg) {
        connections.send(id, ((Command) msg).execute(arg));
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

}
