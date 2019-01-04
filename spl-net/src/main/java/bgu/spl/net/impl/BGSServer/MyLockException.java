package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Colors;

class MyLockException extends IllegalMonitorStateException {
    MyLockException(String message) {
        super(message);
    }

    public void throwMe() {
        System.out.println(Colors.Back_BLUE + Colors.TEXT_BLACK + super.getMessage() + Colors.RESET);
        this.printStackTrace();
        System.exit(62456545);
    }

    public String getMessage() {
        return Colors.TEXT_BLACK+Colors.Back_GREEN+ super.getMessage()+Colors.RESET;
    }
}
