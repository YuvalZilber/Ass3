package bgu.spl.net.impl.BGSServer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Users implements ReadWriteLock {
    private final Set<User> users = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Lock readLock() {
        return lock.readLock();
    }

    @Override
    public Lock writeLock() {
        return lock.writeLock();
    }

    User get(String name) {
        if (name == null) return null;
        User output = null;
        lock.readLock().lock();
        for (User user : users) {
            if (user.getName().equals(name))
                output = user;
        }
        lock.readLock().unlock();
        return output;
    }

    synchronized User get(int id) {
        User output = null;
        lock.readLock().lock();
        for (User user : users) {
            if (user.getId() == id)
                output = user;
        }
        lock.readLock().unlock();
        return output;
    }

    boolean addIfAbsent(String name) {
        return addIfAbsent(name, "");
    }

    boolean addIfAbsent(String name, String password) {
        if (get(name) != null) return false;

        writeLock().lock();//#
        if (get(name) != null) return false;
        User user = new User(name);
        if (!password.equals(""))
            user.setPassword(password);
        boolean output = users.add(user);
        writeLock().unlock();//#

        return output;
    }

    boolean removeIfPossible(String name) {
        writeLock().lock();
        User user = get(name);
        if (user == null)
            return false;
        boolean output = users.remove(user);
        writeLock().unlock();
        return output;
    }

    int size() {
        return users.size();
    }

    String[] getNames() {
        lock.readLock().lock();
        String[] names = new LinkedList<>(users).stream().map(User::getName).toArray(String[]::new);
        lock.readLock().unlock();
        return names;
    }

    List<User> asList() {
        return new LinkedList<>(users);
    }
}
