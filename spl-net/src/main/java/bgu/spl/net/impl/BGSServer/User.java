package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.Messages.MessagePOST;
import bgu.spl.net.impl.BGSServer.Messages.NOTIFICATION;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private int id = -1;
    private boolean loggedIn = false;
    private String name;
    private Users following = new Users();
    private Users followers = new Users();

    ConcurrentLinkedQueue<MessagePOST> posts;
    ConcurrentLinkedQueue<NOTIFICATION> missed;
    
    public Users getFollowing() {
        return following;
    }

    public Users getFollowers() {
        return followers;
    }

    public void setFollowing(Users following) {
        this.following = following;
    }

    public void setFollowers(Users followers) {
        this.followers = followers;
    }

    public void miss(NOTIFICATION post) {
        missed.add(post);
    }

    public User(String name) {
        this.name = name;
    }

    public void post(MessagePOST post) {
        posts.add(post);
    }

    public int getId() {
        return id;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void login(int id) {
        this.loggedIn = true;
        this.id = id;
    }

    public void logout() {
        this.loggedIn = false;
        this.id = -1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean follow(byte todo, User user2follow) {
        boolean didit1;
        boolean didit2 = user2follow.addMeAsFollowerTo(todo, user2follow);
        if (todo == 0) didit1 = following.addIfAbsent(user2follow.name);
        else didit1 = following.removeIfPossible(user2follow.name);
        //todo: delete this from here
        if (didit1 != didit2)
            throw new IllegalStateException("**********846****************");
        //todo: delete this to here
        return didit1;
    }

    private boolean addMeAsFollowerTo(byte todo, User user) {
        if (todo == 0)
            return user.followers.addIfAbsent(this.getName());
        return user.followers.removeIfPossible(this.name);
    }
}
