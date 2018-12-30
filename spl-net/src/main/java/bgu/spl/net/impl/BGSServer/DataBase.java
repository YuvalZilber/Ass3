package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.impl.ConnectionsImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {
    public DataBase() { }

    private ConnectionsImpl<Message> connections = new ConnectionsImpl<>();
    private Users users = new Users();
    ConcurrentHashMap<Integer, MessagePOST> posts;

    public boolean register(String name) {
        return users.addIfAbsent(name);
    }

    public boolean login(String name, int id) {
        return users.login(name, id);
    }

    public boolean logout(int id) {
        return users.logout(id);
    }

    public void follow(int id, byte todo, String[] names) {
        User user = users.get(id);
        boolean didit = false;
        for (String name : names) {
            User toFollow = users.get(name);
            didit |= user.follow(todo, toFollow);
        }
        if (didit) {
            ACK ack = new ACK((short) 4);
            ack.addOptions(names.length, names);
            send(id, ack);
        }
        else {
            ERROR err = new ERROR();
            err.setMessageOpcode((short) 4);
            send(id, err);
        }
    }

    public boolean isUserLoggedIn(int id) {
        if (users.get(id) == null) return false;
        return users.get(id).isLoggedIn();
    }

    public boolean post(int id, MessagePOST post) {
        posts.put(id, post);
        LinkedList<User> users2sendit2 = new LinkedList<>(users.get(id).getFollowers().asList());
        String[] tagged = post.getContent().split(" ");
        tagged = Arrays.stream(tagged).filter(x -> x.charAt(0) == '@' &&
                                                   !users2sendit2.contains(users.get(x.substring(1)))).toArray(String[]::new);
        Arrays.stream(tagged).forEach(x -> users2sendit2.add(users.get(x)));


        NOTIFICATION notification = generateNotification(id, post, (char) 0);

        for (User user : users2sendit2) {
            int userID = user.getId();
            if (user.isLoggedIn()) {
                connections.send(userID, notification);
            }
            else
                user.miss(notification);
        }
        return true;
    }

    public void send(int id, Message msg) {
        connections.send(id, msg);
    }

    public boolean sendPM(int id, MessagePM pm) {
        if (users.get(pm.getUsername()) == null) return false;
        pm.setSender(id2name(id));
        int receiver = name2id(pm.getUsername());

        NOTIFICATION notification = generateNotification(id, pm, (char) 0);

        connections.send(receiver, notification);
        return true;
    }

    private NOTIFICATION generateNotification(int id, ContentHolder holder, char type) {
        NOTIFICATION notification = new NOTIFICATION();
        notification.setContent(holder.getContent());
        notification.setPostinUser(id2name(id));
        notification.setType(type);
        return notification;
    }

    public void userList(int id) {
        ACK ack = new ACK((short) 7);
        String[] names = users.getNames();
        ack.addOptions(names.length, names);
        send(id, ack);
    }

    public void stats(int id, String name) {
        ACK ack = new ACK((short) 8);
        User reqUser = users.get(name);
        ack.addOptions(reqUser.posts.size(), reqUser.getFollowers().size(), reqUser.getFollowing().size());
    }

    private String id2name(int id) {
        return users.get(id).getName();
    }

    private int name2id(String name) {
        return users.get(name).getId();
    }
}