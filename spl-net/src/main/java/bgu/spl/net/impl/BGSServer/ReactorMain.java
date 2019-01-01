package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        Database db = new Database();
        if (args.length == 0)
            args = new String[]{"7777", Runtime.getRuntime().availableProcessors() + ""};


        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), //port
                () -> new bgsProtocol(db), //protocol factory
                bgsEncoderDecoder::new //message encoder decoder factory
                      ).serve();
    }
}
