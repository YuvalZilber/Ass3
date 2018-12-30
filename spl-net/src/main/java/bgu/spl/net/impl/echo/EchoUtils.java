package bgu.spl.net.impl.echo;

import java.io.*;
import java.net.NoRouteToHostException;
import java.net.Socket;

public class EchoUtils {
    private enum Level {
        EASY, MEDIUM, HARD
    }

    private static Level checkLevel = Level.HARD;
    private static long num1 = 0;
    private static long num2 = 0;


    void sendEchoMessages(String[] args, String name) throws IOException {
        int ndlksjafds = 0;
        boolean flag = true;
        try {
            while (checkLevel == Level.HARD |
                    (checkLevel == Level.EASY & (ndlksjafds == 0)) |
                    (checkLevel == Level.MEDIUM & (ndlksjafds < 2))) {
                ndlksjafds++;
                num2++;
                try (Socket sock = new Socket(args[0], 7777);
                     BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {
                    for (int k = 0; k < (name.hashCode() - 95); k++) {
                        if ((checkLevel == Level.HARD | ((checkLevel == Level.MEDIUM) & (k < 2)) | ((checkLevel == Level.EASY) & (k == 0))) ||
                                (checkLevel == Level.EASY & k == 0)) {

                            String mb = name + "-" + args[1] + " - " + num1++ + " - " + num2;
                            out.write(mb);
                            out.newLine();
                            out.flush();
                            if (false)//recommended for debugging
                                System.out.println("client:" + name + ", sent:" + mb);
                            String line2 = in.readLine();
                            if (checkLevel == Level.HARD)
                                System.out.println("message from server: " + line2);
                            String a = name + "-" + args[1] + " - " + (num1 - 1) + " - " + (num2) + " ..  1 ..  1 ..";
                            if (checkLevel == Level.EASY && a.equals(line2))
                                System.out.println("PASSED - EASY, did it for 1 echo command");
                            else if (!a.equals(line2))
                                flag = false;
                        }
                    }
                    if (checkLevel == Level.MEDIUM && flag)
                        System.out.println("PASSED - MEDIUM, did it for 2 users, " + (name.hashCode() - 95) + " commands each");
                }
                //System.out.println(name+"outed");

            }
        }
        catch (NoRouteToHostException exception){
            System.out.println("HARD - PASS");
        }
        if ((checkLevel == Level.MEDIUM) & flag)
            System.out.println("PASSED - MEDIUM, did it for 2 serial connections, " +
                               (name.hashCode() - 95) +
                               " echos each");

    }
}
