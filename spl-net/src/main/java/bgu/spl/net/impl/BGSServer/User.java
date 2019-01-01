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

    @Override
    public Lock readLock() {
        return lock.readLock();
    }

    @Override
    public Lock writeLock() {
        return lock.writeLock();
    }

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
        writeLock().lock();
        if (id == -1)
            this.id = id;
        writeLock().lock();
    }

    void logout() {
        writeLock().lock();
        this.id = -1;
        writeLock().lock();
    }

    boolean follow(byte todo, User user2follow) {
        boolean didit1;

        boolean didit2 = user2follow.addMeAsFollowerTo(todo, user2follow);
        writeLock().lock();
        didit1 = todo == 0 ? following.addIfAbsent(user2follow.getName()) :
                 following.removeIfPossible(user2follow.name);
        writeLock().unlock();
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
