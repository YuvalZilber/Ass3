package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.ConnectionsImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class Database {

    private ConnectionsImpl<Message> connections = null;
    private final Users users = new Users();
    private ConcurrentHashMap<Integer, MessagePOST> posts;

    Database() {
    }

    void initialConnections(ConnectionsImpl<Message> connections) {
        if (this.connections == null)
            this.connections = connections;
    }

    boolean register(String name, String password) {
        return users.addIfAbsent(name, password);
    }

    private void success(int msgOpcode, int id) {//send default ack
        ACK ack = new ACK();
        ack.setMessageOpcode((short) msgOpcode);
        this.send(id, ack);
    }

    private void error(int msgOpcode, int id) {//send error
        ERROR err = new ERROR();
        err.setMessageOpcode((short) msgOpcode);
        this.send(id, err);
    }

    void login(String name, String password, int id) {
        User user;
        users.lockReader(0);//#
        user = getUser(name);
        if (user == null ||getUser(id)!=null|| !user.getPassword().equals(password)||user.getId() != -1) {
            error(2, id);
            users.lockReaderRelease(1);
            return;
        }
        users.lockReaderRelease(1);//#


        user.lockWriter(true);//#
        user.login(id);
        success((short) 2, id);
        ConcurrentLinkedQueue<NOTIFICATION> missed = user.getMissed();
        missed.forEach(notification -> this.send(id, notification));
        user.lockWriterRelease();//#
    }

    void logout(int id) {
        User user = getUserAndError(2, id, true, false, true);
        if (user == null) return;
        user.logout();
        user.lockWriterRelease();
        success(3, id);
    }

    private <T> User getUserAndError(int msgOpcode,
                                     int id,
                                     boolean isLoggedIn,
                                     boolean unlockInTheEnd,
                                     boolean writerLock) {
        User user;
        users.lockReader(0);//#
        user = users.get(id);
        users.lockReaderRelease(1);//#
        boolean toError = user == null & isLoggedIn;
        if (user != null & !toError) {
            if (writerLock) user.lockWriter(true);//#
            else user.lockReader(0);//#
            toError = user.isLoggedIn() != isLoggedIn;
            if (unlockInTheEnd) {
                if (writerLock) user.lockWriterRelease();//#
                else user.lockReaderRelease(1);//#
            }
        }
        if (toError) {
            error(msgOpcode, id);
            if (user != null & !unlockInTheEnd) {
                if (writerLock) user.lockWriterRelease();//#
                else user.lockReaderRelease(1);//#
            }
            user = null;
        }
        return user;
    }

    void follow(int id, byte todo, String[] names) {
        User user = getUserAndError(4, id, true, true, false);
        if (user == null) return;
        boolean didit = false;
        for (String name : names) {
            User toFollow = users.get(name);

            didit |= user.follow(todo, toFollow);
        }

        if (didit) success((short) 4, id);
        else error((short) 4, id);

    }

    void post(int id, MessagePOST post) {
        User sender = getUserAndError(5, id, true, false, false);
        if (sender == null) return;
        posts.put(id, post);
        Set<User> usersFollowing = new HashSet<>(sender.getFollowers().asList());
        sender.lockReaderRelease(1);
        String[] tagged = post.getContent().split(" ");
        tagged = Arrays.stream(tagged).filter(x -> x.charAt(0) == '@').toArray(String[]::new);
        Arrays.stream(tagged).forEach(x -> {
            User reciever = getUser(x);
            if (reciever != null)
                usersFollowing.add(reciever);
        });
        final NOTIFICATION notification = generateNotification(sender.getName(), post, '1');

        usersFollowing.forEach(user -> notify(user, notification));
    }

    private void notify(User user, NOTIFICATION notification) {
        user.lockWriter(true);
        if (user.isLoggedIn())
            connections.send(user.getId(), notification);
        else
            user.miss(notification);
        user.lockWriterRelease();
    }

    void send(int id, Message msg) {
        connections.send(id, msg);
    }

    boolean sendPM(int id, MessagePM pm) {
        User user = getUserAndError(6, id, true, true, false);
        if (user == null) return false;
        User reciever = users.get(pm.getUsername());
        if (reciever == null) return false;
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
        users.lockReader(0);//#
        String[] names = users.getNames();
        users.lockReaderRelease(1);//#
        ack.addOptions(names.length, names);
        send(id, ack);
    }

    void stats(int id, String name) {
        ACK ack = new ACK((short) 8);
        users.lockReader(0);//#
        User reqUser = users.get(name);
        if (reqUser == null) {
            error((short) 8, id);
            users.lockReaderRelease(1);//#
            return;
        }
        users.lockReaderRelease(1);//#
        reqUser.lockReader(0);
        ack.addOptions(reqUser.getPosts().size(), reqUser.getFollowers().size(), reqUser.getFollowing().size());
        send(id, ack);
        reqUser.lockReaderRelease(1);//#
    }

    User getUser(int name) {
        return users.get(name);
    }

    private User getUser(String name) {
        return users.get(name);
    }
}