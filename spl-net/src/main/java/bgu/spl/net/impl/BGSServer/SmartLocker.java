package bgu.spl.net.impl.BGSServer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SmartLocker implements ReadWriteLock {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int readerLocked = 0;
    private int writerLocked = 0;
    private String lastClass = "";
    private int lastLine = -1;

    //region LOCKS
    @Override
    public Lock readLock() {
        System.out.println(Thread.currentThread().getName()+" took reader of "+this.getClass().getSimpleName()+" "+this.hashCode());
        return lock.readLock();
    }

    @Override
    public Lock writeLock() {
        System.out.println(Thread.currentThread().getName()+" took writer of "+this.getClass().getSimpleName()+" "+this.hashCode());
        return lock.writeLock();
    }

    void lockReader() {
        System.out.println(Thread.currentThread().getName()+" - reader lock of "+this.getClass().getSimpleName()+" "+this.hashCode());
        lock.readLock().lock();
        readerLocked++;
    }

    void lockWriter() {
        String a=Thread.currentThread().getStackTrace()[2].getClassName();
        int b=Thread.currentThread().getStackTrace()[2].getLineNumber();
        System.out.println(Thread.currentThread().getName()+" - writer lock of "+this.getClass().getSimpleName()+" "+this.hashCode()+" from:"+a+":"+b);
        lock.writeLock().lock();
        lastClass = Thread.currentThread().getStackTrace()[2].getClassName();
        lastLine = Thread.currentThread().getStackTrace()[2].getLineNumber();
        writerLocked++;
    }

    void lockReaderRelease() {
        System.out.println(Thread.currentThread().getName()+" - reader release of "+this.getClass().getSimpleName()+" "+this.hashCode());
        if (readerLocked == 0) throw new MyLockException("**unlocking: readerLocked: " + 0 + ", not: positive");
        lock.readLock().unlock();
        readerLocked--;
    }

    void lockWriterRelease() {
        String a=Thread.currentThread().getStackTrace()[2].getClassName();
        int b=Thread.currentThread().getStackTrace()[2].getLineNumber();
        System.out.println(Thread.currentThread().getName()+" - writer release of "+this.getClass().getSimpleName()+" "+this.hashCode()+" from:"+a+":"+b);
        if (writerLocked == 0) throw new MyLockException("**  locking: writerLocked: " + writerLocked + ", not" + 0);
        lock.writeLock().unlock();
        writerLocked--;
    }

    boolean isWriterLocked(){
        return writerLocked>0;
    }
}
