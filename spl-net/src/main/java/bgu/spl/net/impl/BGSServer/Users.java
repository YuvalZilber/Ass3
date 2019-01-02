package bgu.spl.net.impl.BGSServer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Users implements ReadWriteLock {
    private final Set<User> users = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int readerLocked = 0;
    private int writerLocked = 0;

    //region LOCKS
    @Override
    public Lock readLock() {
        return lock.readLock();
    }

    @Override
    public Lock writeLock() {
        return lock.writeLock();
    }

    synchronized void lockReader() {
        readLock().lock();
        readerLocked++;
    }

    synchronized void lockWriter() {
        if (writerLocked != 0) throw new MyLockException("**  locking: writerLocked: " + writerLocked + ", not" + 0);
        readLock().lock();
        writerLocked++;
    }

    synchronized void lockReaderRelease() {
        if (readerLocked == 0) throw new MyLockException("**unlocking: readerLocked: " + 0 + ", not: positive");
        readLock().lock();
        readerLocked--;
    }

    synchronized void lockWriterRelease() {
        readLock().lock();
        writerLocked--;
    }

    synchronized void lockReaderRelease(int expected) {
        if (expected != readerLocked) throw new MyLockException(String.format(
                "**  locking: expected: %d, Actual: %d",
                expected, readerLocked));
        readLock().lock();
        readerLocked--;
    }

    synchronized void lockReader(int expected) {
        if (expected != readerLocked) throw new MyLockException(String.format(
                "**  locking: expected: %s, Actual: %d",
                expected < 0 ? "positive" : "" + expected, readerLocked));
        readLock().lock();
        readerLocked++;
    }

    synchronized void lockWriter(boolean tothrow) {
        if (tothrow & (writerLocked == 1 | readerLocked > 0))
            throw new MyLockException("**  locking: writer is locked against expectation");
        writeLock().lock();
        writerLocked++;
    }

    synchronized boolean isReaderLocked() {
        return readerLocked > 0;
    }

    synchronized boolean isWriterLocked() {
        return writerLocked > 0;
    }

    //endregion
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
        lock.readLock().lock();//#
        for (User user : users) {
            if (user.getId() == id)
                output = user;
        }
        lock.readLock().unlock();//#
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
        //lockWriter(true);
        User user = get(name);
        if (user == null)
            return false;
        boolean output = users.remove(user);
        //lockWriterRelease();
        return output;
    }

    int size() {
        return users.size();
    }

    String[] getNames() {
        lockReader(1);
        String[] names = new LinkedList<>(users).stream().map(User::getName).toArray(String[]::new);
        lockReaderRelease(2);
        return names;
    }

    List<User> asList() {
        return new LinkedList<>(users);
    }
}
