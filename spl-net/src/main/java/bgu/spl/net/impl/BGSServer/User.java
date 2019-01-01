package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.Messages.MessagePOST;
import bgu.spl.net.impl.BGSServer.Messages.NOTIFICATION;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class User implements ReadWriteLock {
    private int id = -1;
    private String password = "";
    private final String name;
    private final Users following = new Users();
    private final Users followers = new Users();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ConcurrentLinkedQueue<MessagePOST> posts = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<NOTIFICATION> missed = new ConcurrentLinkedQueue<>();
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
        readerLocked++;
        readLock().lock();
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
        if(expected<0){
            lockReaderRelease();
            return;
        }
        if (expected != readerLocked) throw new MyLockException(String.format(
                "**  locking: expected: %d, Actual: %d",
                expected, readerLocked));
        readLock().lock();
        readerLocked--;
    }

    synchronized void lockReader(int expected) {
        if ((!(expected < 0 & readerLocked > 0)) & (expected != readerLocked)) throw new MyLockException(String.format(
                "**  locking: expected: %s, Actual: %d",
                expected < 0 ? "positive" : "" + expected, readerLocked));
        readLock().lock();
        readerLocked++;
    }

    synchronized void lockWriter(boolean tothrow) {
        if (tothrow & (writerLocked == 1 | readerLocked>0)) throw new MyLockException("**  locking: writer is locked against expectation");
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
    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    ConcurrentLinkedQueue<MessagePOST> getPosts() {
        return posts;
    }

    ConcurrentLinkedQueue<NOTIFICATION> getMissed() {
        return missed;
    }

    Users getFollowing() {
        return following;
    }

    Users getFollowers() {
        return followers;
    }

    synchronized void miss(NOTIFICATION post) {
        missed.add(post);
    }

    User(String name) {
        this.name = name;
    }

    int getId() {
        return id;
    }

    boolean isLoggedIn() { return id != -1; }

    String getName() {
        return name;
    }

    void login(int id) {
        if (id != -1) throw new IllegalStateException("Err 54615");//todo:delete
        this.id = id;
    }

    void logout() {
        this.id = -1;
    }

    boolean follow(byte todo, User user2follow) {
        boolean didit1;

        user2follow.lockReader(0);
        boolean didit2 = user2follow.addMeAsFollowerTo(todo, user2follow);

        lockWriter(true);
        didit1 = todo == 0 ? following.addIfAbsent(user2follow.getName()) :
                 following.removeIfPossible(user2follow.name);
        lockWriterRelease();
        user2follow.lockReaderRelease(1);
        //todo: delete this from here
        if (didit1 != didit2)
            throw new IllegalStateException("**********846****************");
        //todo: delete this to here
        return didit1;
    }

    private boolean addMeAsFollowerTo(byte todo, User user) {
        boolean flag;
        user.writeLock().lock();
        flag = todo == 0 ? user.followers.addIfAbsent(this.getName()) :
               user.followers.removeIfPossible(this.name);
        user.writeLock().unlock();
        return flag;
    }

}
