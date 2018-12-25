package bgu.spl.net.impl.echo;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class EchoClient2 {

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        if (args.length == 0) {
            args = new String[]{"localhost", "222"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {
            while (true) {
                out.write(args[1]);
                out.newLine();
                out.flush();

                String line = in.readLine();
                System.out.println("message from server: " + line);
            }
        }
    }
}
