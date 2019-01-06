package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.ConnectionsImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class Database {

    private ConnectionsImpl<Message> connections = null;
    private final Users users = new Users();
    private ConcurrentHashMap<Integer, Message> posts = new ConcurrentHashMap<>();

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
        users.lockReader();//#
        user = getUser(name);
        if (user == null || getUser(id) != null) {
            error(2, id);
            users.lockReaderRelease();
            return;
        }
        users.lockReaderRelease();//#


        user.lockWriter();//#
        if (!user.getPassword().equals(password) || user.getId() != -1) {
            error(2, id);
            user.lockWriterRelease();
            return;
        }
        user.login(id);
        success((short) 2, id);
        ConcurrentLinkedQueue<NOTIFICATION> missed = user.getMissed();
        missed.forEach(notification -> this.send(id, notification));
        user.lockWriterRelease();//#
    }

    void logout(int id) {
        User user = getUserAndError(2, id, false, true);
        if (user == null) return;
        user.logout();
        user.lockWriterRelease();
        success(3, id);
    }

    private User getUserAndError(int msgOpcode,
                                 int id,
                                 boolean unlockInTheEnd,
                                 boolean writerLock) {
        User user;
        users.lockReader();//#
        user = users.get(id);
        users.lockReaderRelease();//#
        boolean toError = user == null;
        if (user != null & !toError) {
            if (writerLock) user.lockWriter();//#
            else user.lockReader();//#
            toError = !user.isLoggedIn();
            if (unlockInTheEnd) {
                if (writerLock) user.lockWriterRelease();//#
                else user.lockReaderRelease();//#
            }
        }
        if (toError) {
            error(msgOpcode, id);
            if (user != null & !unlockInTheEnd) {
                if (writerLock) user.lockWriterRelease();//#
                else user.lockReaderRelease();//#
            }
            user = null;
        }
        return user;
    }

    void follow(int id, byte todo, String[] names) {
        User user = getUserAndError(4, id, true, false);
        if (user == null) return;
        Users didit = new Users();
        for (String name : names) {
            User toFollow = users.get(name);
            if(toFollow!=null)
                if (user.follow(todo, toFollow))
                    didit.addIfAbsent(toFollow.getName());
        }

        if (didit.size() > 0) {
            ACK ack = new ACK((short) 4);
            ack.addOptions((short) didit.size());
            List<User> tmp = didit.asList();
            tmp.forEach(x -> ack.addOptions(x.getName()));
            connections.send(id, ack);
        }
        else error((short) 4, id);

    }

    void post(int id, MessagePOST post) {
        User sender = getUserAndError(5, id, false, true);
        if (sender == null) return;
        posts.put(id, post);
        Set<User> usersFollowing = new HashSet<>(sender.getFollowers().asList());
        sender.getPosts().add(post);
        sender.lockWriterRelease();

        String[] tagged = post.getContent().split(" ");
        tagged = Arrays.stream(tagged).filter(x -> x.length()>0 && x.charAt(0) == '@').toArray(String[]::new);
        Arrays.stream(tagged).forEach(x -> {
            User reciever = getUser(x.substring(1));
            if (reciever != null) {
                boolean good = true;
                for (User exist : usersFollowing) {
                    if (exist.getName().equals(reciever.getName()))
                        good = false;
                }
                if (good)
                    usersFollowing.add(reciever);
            }
        });
        final NOTIFICATION notification = generateNotification(sender.getName(), post, (byte) 1);

        usersFollowing.forEach(user -> notify(user, notification));
        success(5, id);
    }

    private void notify(User user, NOTIFICATION notification) {
        user = users.get(user.getName());
        user.lockWriter();
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
        posts.put(id,pm);
        User sender = getUserAndError(6, id, true, false);
        if (sender == null) return false;
        User reciever = users.get(pm.getUsername());
        if (reciever == null) return false;
        NOTIFICATION notification = generateNotification(sender.getName(), pm, (byte) 0);

        notify(reciever, notification);
        return true;
    }

    private NOTIFICATION generateNotification(String name, ContentHolder holder, byte type) {
        NOTIFICATION notification = new NOTIFICATION();
        notification.setContent(holder.getContent());
        notification.setPostinUser(name);
        notification.setType(type);
        return notification;
    }

    void userList(int id) {
        ACK ack = new ACK((short) 7);
        users.lockReader();//#
        String[] names = users.getNames();
        users.lockReaderRelease();//#
        ack.addOptions((short) names.length, names);
        send(id, ack);
    }

    void stats(int id, String name) {
        ACK ack = new ACK((short) 8);
        users.lockReader();//#
        User reqUser = users.get(name);
        if (reqUser == null) {
            error((short) 8, id);
            users.lockReaderRelease();//#
            return;
        }
        users.lockReaderRelease();//#
        reqUser.lockReader();
        ack.addOptions((short) reqUser.getPosts().size(),
                       (short) reqUser.getFollowers().size(),
                       (short) reqUser.getFollowing().size());
        send(id, ack);
        reqUser.lockReaderRelease();//#
    }

    User getUser(int name) {
        return users.get(name);
    }

    private User getUser(String name) {
        return users.get(name);
    }
}