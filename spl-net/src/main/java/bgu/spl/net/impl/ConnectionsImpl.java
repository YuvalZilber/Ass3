package bgu.spl.net.impl;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.IsCloseable;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConnectionsImpl<T> implements Connections<T> {
    private HashMap<Integer, ConnectionHandler<T>> clients = new HashMap<>();
    private int id = -1;
    private boolean isCleaning = false;
    @Override
    public boolean send(int connectionId, T msg) {
        try {
            synchronized (clients) {
                clients.get(connectionId).send(msg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public void broadcast(T msg) {
        clients.values().forEach(client -> client.send(msg));
    }


    @Override
    public void disconnect(int connectionId) {
        //try {
        synchronized (clients) {
            clients.remove(connectionId);
        }
        //    try {
//            } finally {
//                client.close();
//            }
//        } catch (IOException ignored) {
//        }
    }

    public void disconnectAll() {
        for (ConnectionHandler<T> tConnectionHandler : clients.values())
            try {
                tConnectionHandler.close();

            } catch (IOException ignored) {
            }
        clients.values().forEach(client -> {
            try (ConnectionHandler con = client) {
            } catch (IOException ignored) {
            }
        });
        clients.clear();
    }

    public int add(ConnectionHandler<T> connection) {
        cleanClosed();
        synchronized (clients) {
            if (!clients.containsValue(connection)) {
                clients.put(++id, connection);
                return id;
            }

            for (Map.Entry<Integer, ConnectionHandler<T>> entry : clients.entrySet())
                if (entry.getValue() == connection)
                    return entry.getKey();
        }
        return -1;
    }

    private void cleanClosed() {
        HashMap<Integer, ConnectionHandler<T>> copy = new HashMap<>();
        synchronized (clients) {
            copy = new HashMap<>(clients);
        }
        for (Map.Entry<Integer, ConnectionHandler<T>> entry : copy.entrySet()) {
            IsCloseable ic = (IsCloseable) entry.getValue();
            if (ic.isClosed()) {
                disconnect(entry.getKey());
            }
        }
    }

    public int size() {
        synchronized (clients) {
            return clients.size();
        }
    }
}