package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Colors;

class MyLockException extends IllegalMonitorStateException {
    public MyLockException(String message) {
        super(message);
    }

    public void throwMe() {
        System.out.println(Colors.Back_BLUE + Colors.TEXT_WHITE + super.getMessage() + Colors.RESET);
        this.printStackTrace();
        System.exit(62456545);
    }

    public String getMessage() {
        return Colors.Back_BLUE+Colors.TEXT_WHITE+ super.getMessage()+Colors.RESET;
    }
}
