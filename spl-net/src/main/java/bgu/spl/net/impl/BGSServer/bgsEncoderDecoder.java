package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGSServer.Messages.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class bgsEncoderDecoder implements MessageEncoderDecoder {
    //region Decoder
    private final LinkedList<Byte> bytes = new LinkedList<>();//bytse red
    private Class<? extends Message> curType = null;//current message type
    private int remains0 = 0;//the '\0' remains before EOM(End Of Message)
    private int remainsForParam = 0;//remain bytes before end of parameter
    private int paramIndex = -1;//the current parameter index
    private Field[] params = null;//array of Message fields
    private final LinkedList<Integer> splitters = new LinkedList<>();//indexes in bytes where a new field begins


    @SuppressWarnings("all")
    @Override
    public Message decodeNextByte(byte nextByte) {
        bytes.add(nextByte);
        if (params == null) {//before discover message type
            if (bytes.size() == 2) {/**if got the seccond {@code: opcode} byte*/
                Short code = ByteBuffer.wrap(new byte[]{bytes.get(0), bytes.get(1)}).getShort();
                //initiate collections
                bytes.clear();
                splitters.clear();
                curType = getClassByOpcode(code);
                params = curType.getDeclaredFields();
                //initiate indexes & counters
                remains0 = 0;
                paramIndex = -1;
                remainsForParam = 0;
            }
        }
        else {
            if (nextByte == 0 && field2length(params[paramIndex]) < 0)
                remains0--;
            if (remainsForParam > 0)
                remainsForParam--;
        }
        //if it is the last byte of the facking message
        if (((nextByte == 0 & remains0 == 0 & remainsForParam < 0) | remainsForParam == 0) &
            (params != null && paramIndex == params.length - 1)) {
            byte[] arr = new byte[bytes.size()];
            remains0 = 0;
            bytes.forEach(b -> arr[remains0++] = b);
            Message msg =
                    messageGenerator(arr, curType, splitters.toArray(new Integer[0]));
            params = null;
            bytes.clear();
            return msg;
        }
        //if it is the last byte of current parameter
        else if (params != null &&
                 (remainsForParam == 0 || (nextByte == 0 && params[paramIndex].getType() == String.class))) {
            splitters.add(bytes.size());
            paramIndex++;
            remainsForParam = field2length(params[paramIndex]);
            if (params[paramIndex].getType() == String.class) {//if u r a string and NOT the last field u own a zero;
                remains0++;
            }

            if (params[paramIndex].getType() == String[].class)
                remains0 += ByteBuffer.wrap(new byte[]{bytes.get(1), bytes.get(2)}).getShort();
        }

        return null;
    }

    //region Decoder Utils
    private static int field2length(Field field) {
        Class<?> type = field.getType();
        if (type == Short.class || type == short.class || type == char.class)
            return 2;
        if (type == Byte.class || type == byte.class)
            return 1;
        if (type == String.class)
            return -1;
        if (type == String[].class)
            return -2;
        return 0;
    }

    private Class<? extends Message> getClassByOpcode(Short opcode) {
        switch (opcode) {
            case 1: return MessageREGISTER.class;
            case 2: return MessageLOGIN.class;
            case 3: return MessageLOGOUT.class;
            case 4: return MessageFOLLOW.class;
            case 5: return MessagePOST.class;
            case 6: return MessagePM.class;
            case 7: return MessageUSERLIST.class;
            case 8: return MessageSTAT.class;
            case 9: return NOTIFICATION.class;
            case 10: return ACK.class;
            case 11: return ERROR.class;
            default: return null;
        }
    }

    //START DECODING METHODS

    @SuppressWarnings("unchecked")
    private static <T> T messageGenerator(byte[] bytes, Class<T> type, Integer[] splitters) {
        try {
            Field[] fields = type.getDeclaredFields();
            Object[] fieldsValues = bytes2values(bytes, splitters, fields);

            Constructor<T> constructor = (Constructor<T>) type.getConstructors()[0];
            T output = constructor.newInstance();

            for (int i = 0; i < fieldsValues.length; i++) {
                Field field = fields[i];
                Object value = fieldsValues[i];
                field.setAccessible(true);
                field.set(output, value);
            }

            return output;
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object[] bytes2values(byte[] bytes, Integer[] splitIndexes, Field[] fields) {
        Object[] values = new Object[fields.length];
        LinkedList<Integer> tmp = new LinkedList<>(Arrays.asList(splitIndexes));
        tmp.add(bytes.length);
        splitIndexes = tmp.toArray(new Integer[0]);

        for (int i = 0; i < splitIndexes.length - 1; i++) {
            int from = splitIndexes[i], to = splitIndexes[i + 1];
            byte[] valueAsBytes = Arrays.copyOfRange(bytes, from, to);
            values[i] = bytes2value(valueAsBytes, fields[i].getType());
        }
        return values;
    }

    private static Object bytes2value(byte[] bytes, Class type) {
        if (type == short.class | type == Short.class | type == char.class) return ByteBuffer.wrap(bytes).getShort();
        if (type == byte.class | type == Byte.class) return bytes[0];
        if (type == String.class) return new String(bytes, StandardCharsets.UTF_8).replaceAll("\0", "");
        if (type == String[].class) return new String(bytes, StandardCharsets.UTF_8).split("\0");
        return null;
    }

    //endregion
    //endregion

    //region Encoder

    @Override
    public byte[] encode(Object obj) {
        if (!(obj instanceof Message)) return null;
        Message msg = (Message) obj;
        System.out.println("encoding: "+msg.toString());
        byte[] bytes = value2bytes(msg.opCode);
        if (!(msg instanceof ACK)) {
            Object[] values = message2values(msg);
            bytes = concat(bytes, values2bytes(values));
        }
        else {
            ACK ack = (ACK) msg;
            bytes = concat(bytes, value2bytes(ack.getMessageOpcode()));
            bytes = concat(bytes, values2bytes(ack.getOptional()));
        }
        return bytes;
    }

    private static Object[] message2values(Message msg) {
        Field[] fields = msg.getClass().getDeclaredFields();
        List<Object> values = new LinkedList<>();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                values.add(field.get(msg));
            }
            catch (IllegalAccessException a) {
                a.printStackTrace();
            }
        }
        return values.toArray(new Object[0]);
    }

    private static byte[] values2bytes(Object[] values) {
        byte[] bytes = new byte[0];
        for (Object o : values) bytes = concat(bytes, value2bytes(o));
        return bytes;
    }

    private static byte[] value2bytes(Object value) {
        Class type = value.getClass();
        if (type == short.class | type == Short.class | type == char.class)
            return new byte[]{(byte) ((short) value >> 8),(byte) ((short)value)};
        if (type == byte.class | type == Byte.class) return new byte[]{(byte) value};
        if (type == String.class) return ((String) value + '\0').getBytes(Charset.forName("UTF8"));
        if (type == String[].class & ((String[]) value).length == 0) return new byte[0];
        if (type == String[].class) return concat(value2bytes(((String[]) value)[0]),
                                                  value2bytes(Arrays.copyOfRange(((String[]) value), 1,((String[]) value).length)));
        return new byte[0];
    }

    private static byte[] concat(byte[] arr1, byte[] arr2) {
        byte[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;

    }

    //endregion
}
