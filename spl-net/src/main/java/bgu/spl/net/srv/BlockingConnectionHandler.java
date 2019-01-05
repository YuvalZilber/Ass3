package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T>, IsCloseable {
    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;


            BufferedInputStream in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!isClosed() && (read = in.read()) >= 0) {
                System.out.println("red: "+read);
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println("decoded: "+nextMessage.toString());
                    protocol.process(nextMessage);
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            try {
                close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        sock.close();
        connected = false;
    }

    @Override
    public void send(T msg) {
        try {
            System.out.println("send: "+msg);
            out.write(encdec.encode(msg));
            out.flush();
            if (protocol.shouldTerminate()) close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {//maybe must delete
        boolean output = sock.isClosed();
        connected = !output;
        return output;
    }

    public BidiMessagingProtocol<T> getProtocol() {//maybe must delete
        return protocol;
    }
}
