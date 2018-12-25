package bgu.spl.net.impl.echo;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Server;

import java.io.IOException;

public class EchoServerMain {

    public static void main(String[] args) {
// you can use any server...
        System.out.print("do you want a BLOCKING server? [Y/n] ");
        boolean blocking = true;
        try {
            char c = (((char) System.in.read()) + "").toLowerCase().charAt(0);
            blocking = c == 'y';
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (blocking) {
            Connections<String> connections = new ConnectionsImpl<>();
            Server.threadPerClient(
                    7777, //port
                    EchoProtocol::new, //protocol factory
                    LineMessageEncoderDecoder::new //message encoder decoder factory
            ).serve();
        } else {
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    7777, //port
                    EchoProtocol::new, //protocol factory
                    LineMessageEncoderDecoder::new //message encoder decoder factory
            ).serve();
        }
    }
}
