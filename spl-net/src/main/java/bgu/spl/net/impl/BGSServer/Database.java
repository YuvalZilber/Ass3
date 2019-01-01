package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.ConnectionsImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class Database {


    private final ConnectionsImpl<Message> connections = new ConnectionsImpl<>();
    private final Users users = new Users();
    private ConcurrentHashMap<Integer, MessagePOST> posts;

    Database() {

    }

    boolean register(String name, String password) {
        return users.addIfAbsent(name, password);
    }

    private void success(short msgOpcode, int id) {//send default ack
        ACK ack = new ACK();
        ack.setMessageOpcode(msgOpcode);
        this.send(id, ack);
    }

    private void error(short msgOpcode, int id) {//send error
        ERROR err = new ERROR();
        err.setMessageOpcode(msgOpcode);
        this.send(id, err);
    }

    void login(String name, String password, int id) {
        User user;
        users.readLock().lock();//#
        user = getUser(name);
        if (user == null || !user.getPassword().equals(password)) {
            error((short) 2, id);
            return;
        }
        users.readLock().unlock();//#
        success((short) 2, id);

        user.writeLock().lock();//#
        user.login(id);
        ConcurrentLinkedQueue<NOTIFICATION> missed = user.getMissed();
        missed.forEach(notification -> this.send(id, notification));
        user.writeLock().unlock();//#
    }

    boolean logout(int id) {
        User user = users.get(id);

        user.writeLock().lock();//#
        if (!user.isLoggedIn()) return false;
        if (user.getId() == -1) return false;
        user.logout();
        user.writeLock().unlock();//#
        return true;
    }

    void follow(int id, byte todo, String[] names) {
        User user = users.get(id);
        boolean didit = false;
        for (String name : names) {
            users.readLock().lock();//#
            User toFollow = users.get(name);
            users.readLock().unlock();//#
            user.writeLock().lock();//#
            didit |= user.follow(todo, toFollow);
            user.writeLock().unlock();//#
        }

        if (didit) success((short) 4, id);
        else error((short) 4, id);

    }

    void post(int id, MessagePOST post) {

        posts.put(id, post);
        users.readLock().lock();//#
        User sender = users.get(id);
        Set<User> usersFollowing = new HashSet<>(users.get(id).getFollowers().asList());
        users.readLock().unlock();//#
        String[] tagged = post.getContent().split(" ");
        tagged = Arrays.stream(tagged).filter(x -> x.charAt(0) == '@').toArray(String[]::new);
        Arrays.stream(tagged).forEach(x -> usersFollowing.add(users.get(x)));

        final NOTIFICATION notification = generateNotification(sender.getName(), post, '1');

        usersFollowing.forEach(user -> notify(user, notification));
    }

    private void notify(User user, NOTIFICATION notification) {
        user.writeLock().lock();
        if (user.isLoggedIn())
            connections.send(user.getId(), notification);
        else
            user.miss(notification);
        user.writeLock().lock();
    }

    void send(int id, Message msg) {
        connections.send(id, msg);
    }

    boolean sendPM(int id, MessagePM pm) {
        User user = users.get(id);
        users.readLock();
        User reciever = users.get(pm.getUsername());
        if(reciever==null)return false;
        users.writeLock();
        NOTIFICATION notification = generateNotification(pm.getUsername(), pm, '0');

        notify(user, notification);
        return true;
    }

    private NOTIFICATION generateNotification(String name, ContentHolder holder, char type) {
        NOTIFICATION notification = new NOTIFICATION();
        notification.setContent(holder.getContent());
        notification.setPostinUser(name);
        notification.setType(type);
        return notification;
    }

    void userList(int id) {
        ACK ack = new ACK((short) 7);
        users.readLock().lock();//#
        String[] names = users.getNames();
        users.readLock().unlock();//#
        ack.addOptions(names.length, names);
        send(id, ack);
    }

    void stats(int id, String name) {
        ACK ack = new ACK((short) 8);
        users.readLock().lock();//#
        User reqUser = users.get(name);
        if (reqUser == null) {
            error((short) 8, id);
            return;
        }
        users.readLock().unlock();//#
        ack.addOptions(reqUser.getPosts().size(), reqUser.getFollowers().size(), reqUser.getFollowing().size());
        send(id, ack);
    }

    User getUser(int name) {
        return users.get(name);
    }

    private User getUser(String name) {
        return users.get(name);
    }

    Users getUsers() {
        return users;
    }
}