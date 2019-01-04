package bgu.spl.net.impl.BGSServer;

import javax.xml.bind.SchemaOutputResolver;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class Users extends SmartLocker{
    private final Set<User> users = new HashSet<>();

    //endregion
    User get(String name) {
        if (name == null) return null;
        User output = null;
        lockReader();
        for (User user : users) {
            if (user.getName().equals(name))
                output = user;
        }
        lockReaderRelease();
        return output;
    }

    User get(int id) {
        User output = null;
        lockReader();//#
        for (User user : users) {
            if (user.getId() == id)
                output = user;
        }
        lockReaderRelease();//#
        return output;
    }

    boolean addIfAbsent(String name) {
        return addIfAbsent(name, "");
    }

    boolean addIfAbsent(String name, String password) {
        if (get(name) != null) return false;

        lockWriter();//#
        System.out.println(Thread.currentThread().getName()+" "+isWriterLocked());
        if (get(name) != null) {
            return false;
        }
        System.out.println(Thread.currentThread().getName()+" "+isWriterLocked());
        User user = new User(name);
        if (!password.equals(""))
            user.setPassword(password);
        System.out.println(Thread.currentThread().getName()+" "+isWriterLocked());
        boolean output = users.add(user);
        System.out.println(Thread.currentThread().getName()+" "+isWriterLocked());
        lockWriterRelease();//#

        return output;
    }

    boolean removeIfPossible(String name) {
        //lockWriter();
        User user = get(name);
        if (user == null)
            return false;
        //lockWriterRelease();
        return users.remove(user);
    }

    int size() {
        return users.size();
    }

    String[] getNames() {
        lockReader();
        String[] names = new LinkedList<>(users).stream().map(User::getName).toArray(String[]::new);
        lockReaderRelease();
        return names;
    }

    List<User> asList() {
        return new LinkedList<>(users);
    }
}
