package bgu.spl.net.impl.BGSServer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class bgsEncoderDecoderTest {
    static bgsEncoderDecoder decoder = new bgsEncoderDecoder();

    static Map<byte[], String> a = new LinkedHashMap<>();
    private static final Map<String, String> myMap;

    static {
        myMap = new HashMap<String, String>();
        myMap.put("00 04 01 00 01 4d 6f 72 74 79 00", "FOLLOW 1 1 [Morty]");
        myMap.put("00 04 01 00 02 4d 6f 72 74 79 00 61 31 32 33 00", "FOLLOW 1 2 [Morty, a123]");
        myMap.put("00 04 00 00 02 4d 6f 72 74 79 00 61 31 32 33 00", "FOLLOW 0 2 [Morty, a123]");
        myMap.put("00 04 01 00 03 4d 6f 72 74 79 00 61 31 32 33 00 79 75 76 61 6c 00",
                  "FOLLOW 1 3 [Morty, a123, yuval]");
        myMap.put("00 01 4d 6f 72 74 79 00 61 31 32 33 00", "REGISTER Morty a123");
        myMap.put("00 02 4d 6f 72 74 79 00 61 31 32 33 00", "LOGIN Morty a123");
        myMap.put("00 03", "LOGOUT");
        myMap.put("00 08 4d 6f 72 74 79 00", "STAT Morty");
        myMap.put(
                "00 05 4e 6f 62 6f 64 79 20 65 78 69 73 74 73 20 6f 6e 20 70 75 72 70 6f 73 65 2c 20 6e 6f 62 6f 64 79 20 62 65 6c 6f 6e 67 73 20 61 6e 79 77 68 65 72 65 2c 20 65 76 65 72 79 62 6f 64 79 e2 80 99 73 20 67 6f 6e 6e 61 20 64 69 65 2e 20 43 6f 6d 65 20 77 61 74 63 68 20 54 56 2e 00",
                "POST Nobody exists on purpose, nobody belongs anywhere, everybody’s gonna die. Come watch TV.");
        for (Map.Entry<String, String> entry : myMap.entrySet()) {
            String input = entry.getKey();
            String[] tmparr = input.split(" ");
            byte[] bytes = new byte[tmparr.length];
            for (int i = 0; i < tmparr.length; i++) {
                bytes[i] = (byte) ((Character.digit(tmparr[i].charAt(0), 16) << 4)
                                   + Character.digit(tmparr[i].charAt(1), 16));
            }
            a.put(bytes, entry.getValue());
        }
    }

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void decodeNextByte() {
        int kkk = 0;
        for (Map.Entry<byte[], String> entry : a.entrySet()) {
            byte[] bytes = entry.getKey();
            Message output = null;
            for (int i = 0; i < bytes.length; i++) {
                try {
                    output = decoder.decodeNextByte(bytes[i]);
                }
                catch (Exception e) {
                    System.out.println("kkk: " + kkk + ", i:" + i);
                    e.printStackTrace();
                    fail();
                }
                if (i < bytes.length - 1)
                    assertNull(output);
            }
            assertNotNull(kkk + " " + entry.getValue(), output);
            assertEquals(kkk + " " + entry.getValue(), entry.getValue(), output.toString());
            kkk++;
        }
    }

    @org.junit.Test
    public void encode() {
        for (Map.Entry<byte[], String> entry : a.entrySet()) {
            byte[] bytes1 = entry.getKey();
            Message output = null;
            for (int i = 0; i < bytes1.length; i++) {
                try {
                    output = decoder.decodeNextByte(bytes1[i]);
                }
                catch (Exception e) {
                    System.out.println("******************************************");
                }
            }
            byte[] bytes2 = decoder.encode(output);
            assertEquals(bytes1, bytes2);
        }
    }
}