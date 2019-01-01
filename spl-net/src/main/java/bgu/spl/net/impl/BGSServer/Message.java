package bgu.spl.net.impl.BGSServer;

public abstract class Message {
    
    protected final Short opCode;

    public Message(short opCode) {
        this.opCode = opCode;
    }

    public short getOpcode() {
        return opCode;
    }
}
