package bgu.spl.net.impl.BGSServer;

public abstract class Message {
    
    final Short opCode;

    public Message(short opCode) {
        this.opCode = opCode;
    }

    public short getOpcode() {
        return opCode;
    }
}
