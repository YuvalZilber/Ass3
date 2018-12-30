package bgu.spl.net.srv;

import java.io.Closeable;

public interface IsCloseable extends Closeable {

    boolean isClosed();

}
