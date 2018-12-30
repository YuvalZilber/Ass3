package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        String input;
        input = "00 04 01 00 02 4d 6f 72 74 79 00 61 31 32 33 00";//FOLLOW 1 2 [Morty, a123]
        input = "00 04 01 00 03 4d 6f 72 74 79 00 61 31 32 33 00 79 75 76 61 6c 00";//FOLLOW 1 3 [Morty, a123, yuval]
        input = "00 01 4d 6f 72 74 79 00 61 31 32 33 00";//REGISTER Morty a123
        input = "00 02 4d 6f 72 74 79 00 61 31 32 33 00";//LOGIN Morty a123
        input = "00 03";//LOGIN Morty a123
        String[] tmparr = input.split(" ");
        byte[] bytes = new byte[tmparr.length];
        for (int i = 0; i < tmparr.length; i++) {
            bytes[i] = (byte) ((Character.digit(tmparr[i].charAt(0), 16) << 4)
                               + Character.digit(tmparr[i].charAt(1), 16));
        }
        //byte[] toadd="hello".getBytes();

        //byte[] bytes=new byte[]{(byte)0x00, (byte)0x08,(byte)0x4d, (byte)0x6f, (byte)0x72,(byte)0x74,(byte)0x79,(byte)0x00};


        bgsEncoderDecoder decoder = new bgsEncoderDecoder();
        for (int i = 0; i < bytes.length; i++) {
            Message output = decoder.decodeNextByte(bytes[i]);
            if (output != null)
                System.out.println(output);
        }
        DataBase db = new DataBase();
        if (args.length == 0)
            args = new String[]{"7777"};

        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new bgsProtocol(db), //protocol factory
                bgsEncoderDecoder::new //message encoder decoder factory
                              ).serve();
    }
}
