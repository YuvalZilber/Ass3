package bgu.spl.net.impl.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

import static bgu.spl.net.impl.echo.EchoUtils.sendEchoMessages;

public class EchoClient {


    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            args = new String[]{"localhost", "11"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        sendEchoMessages(args, "a");
    }
}
