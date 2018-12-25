package bgu.spl.net.impl.echo;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class EchoClient2 {

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        if (args.length == 0) {
            args = new String[]{"localhost", "22"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        sendEchoMessages(args, "a");
        return;
    }

    static void sendEchoMessages(String[] args, String name) throws IOException {

        while (true) {
            try (Socket sock = new Socket(args[0], 7777);
                 BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

                for (int k = 0; k < (name.hashCode() - 95); k++) {
                    out.write(name + "-" + args[1]);
                    out.newLine();
                    out.flush();

                    String line = in.readLine();
                    System.out.println("message from server: " + line);
                }
            }
        }
    }
}
