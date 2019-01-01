package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        Database db = new Database();
        if (args.length == 0)
            args = new String[]{"7777"};

        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new bgsProtocol(db), //protocol factory
                bgsEncoderDecoder::new //message encoder decoder factory
                              ).serve();
    }
}
