package bgu.spl.net.impl.BGSServer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SmartLocker {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int readerLocked = 0;
    private int writerLocked = 0;
    private String lastClass = "";
    private int lastLine = -1;

    //region LOCKS

    void lockReader() {
        System.out.println(Thread.currentThread().getName()+" - reader lock of "+this.getClass().getSimpleName()+" "+this.hashCode());
        lock.readLock().lock();
        readerLocked++;
    }

    void lockWriter() {
        lock.writeLock().lock();
        lastClass = Thread.currentThread().getStackTrace()[2].getClassName();
        lastLine = Thread.currentThread().getStackTrace()[2].getLineNumber();
        writerLocked++;
    }

    void lockReaderRelease() {
        lock.readLock().unlock();
        readerLocked--;
    }

    void lockWriterRelease() {
        lock.writeLock().unlock();
        writerLocked--;
    }

    boolean isWriterLocked(){
        return writerLocked>0;
    }
}
