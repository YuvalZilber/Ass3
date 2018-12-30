package bgu.spl.net.impl.BGSServer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

class Users {
    private ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<>();

    User get(String name) {
        if (name == null) return null;

        for (User user : users) {
            if (user.getName().equals(name))
                return user;
        }
        return null;
    }

    User get(int id) {
        for (User user : users) {
            if (user.getId() == id)
                return user;
        }
        return null;
    }

    synchronized boolean addIfAbsent(String name) {
        if (get(name) != null) return false;
        User user = new User(name);
        users.add(user);
        return true;
    }

    synchronized boolean login(String name, int id) {
        User user = get(name);
        if (user == null) return false;
        if (user.isLoggedIn()) return false;
        if (user.getId() != -1) return false;
        user.login(id);
        return true;
    }

    synchronized boolean logout(int id) {
        User user = get(id);
        if (user == null) return false;
        if (!user.isLoggedIn()) return false;
        if (user.getId() == -1) return false;
        user.logout();
        return true;
    }


    boolean removeIfPossible(String name) {
        User user = get(name);
        if (user == null) {
            return false;
        }
        return users.remove(user);
    }

    public int size() {
        return users.size();
    }

    public String[] getNames() {
        return new LinkedList<>(users).stream().map(User::getName).toArray(String[]::new);
    }

    public List<User> asList() {
        return new LinkedList<>(users);
    }
}
