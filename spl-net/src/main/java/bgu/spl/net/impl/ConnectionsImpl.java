package bgu.spl.net.impl;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.IsCloseable;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class ConnectionsImpl<T> implements Connections<T> {
    private final HashMap<Integer, ConnectionHandler<T>> clients = new HashMap<>();
    private final AtomicInteger id = new AtomicInteger(0);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public boolean send(int connectionId, T msg) {
        try {
            lock.readLock().lock();
            ConnectionHandler<T> handler = clients.get(connectionId);
            lock.readLock().unlock();
            handler.send(msg);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void broadcast(T msg) {
        lock.readLock().lock();
        clients.values().forEach(client -> client.send(msg));
        lock.readLock().unlock();
    }

    @Override
    public void disconnect(int connectionId) {
        //try {
        lock.writeLock().lock();
        ConnectionHandler<T> handler=clients.remove(connectionId);

        lock.writeLock().unlock();
        //    try {
//            } finally {
//                client.close();
//            }
//        } catch (IOException ignored) {
//        }
    }

    public void disconnectAll() {
        lock.writeLock().lock();
        for (ConnectionHandler<T> tConnectionHandler : clients.values())
            try {
                tConnectionHandler.close();
            }
            catch (IOException ignored) {
            }
        clients.clear();
        lock.writeLock().unlock();
    }

    public int add(ConnectionHandler<T> connection) {
        cleanClosed();
        int curID = id.getAndIncrement();
        lock.writeLock().lock();
        clients.put(curID, connection);
        lock.writeLock().unlock();

        return curID;
    }

    private void cleanClosed() {
        HashMap<Integer, ConnectionHandler<T>> copy;
        lock.readLock().lock();
        copy = new HashMap<>(clients);
        lock.readLock().unlock();

        for (Map.Entry<Integer, ConnectionHandler<T>> entry : copy.entrySet()) {
            IsCloseable ic = (IsCloseable) entry.getValue();
            if (ic.isClosed()) {
                disconnect(entry.getKey());
            }
        }

    }

    public int size() {
        lock.readLock().lock();
        int s = clients.size();
        lock.readLock().unlock();
        return s;
    }
}