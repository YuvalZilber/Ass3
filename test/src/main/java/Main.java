import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        A a=getObject(A.class,new String[]{"aShort1" ,"aShort2","string" ,"aBoolean1","aBoolean2"},new Object[]{(short)5,(short)7,"kkk",true,false});
        System.out.println(a);
    }


    private static void putField(Object obj, String fieldName, Object value) {
        Class<?> type = obj.getClass();
        try {
            type.getDeclaredMethod("set"+(fieldName.charAt(0)+"").toUpperCase()+fieldName.substring(1),value.getClass()).invoke(obj,value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            try {
                type.getDeclaredMethod("set"+fieldName,value.getClass()).invoke(obj,value);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }

        }
    }
    @SuppressWarnings("unchecked")
    private static <T> T getObject(Class<T> type, String[] fieldsNames, Object[] fieldsValues) {
        Constructor<T> constructor = null;
        T output = null;
        try {
            constructor = (Constructor<T>) type.getConstructors()[0];
            output = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        for (int i = 0; i < fieldsValues.length; i++) {
            String name = fieldsNames[i];
            Object value = fieldsValues[i];
            putField(output, name, value);
        }
        return output;
    }

    private static Object[] getValues(byte[] bytes, Integer[] splitIndexes, Class<?>[] types) {
        Object[] output = new Object[types.length];
        List<Integer> tmp = Arrays.asList(splitIndexes);
        tmp.add(bytes.length);
        splitIndexes = tmp.toArray(new Integer[0]);

        for (int i = 0; i < splitIndexes.length - 1; i++) {
            int index_from = splitIndexes[i],
                    index_to = splitIndexes[i + 1];
            byte[] valueAsBytes = Arrays.copyOfRange(bytes, index_from, index_to);
            output[i]=bytes2type(valueAsBytes,types[i]);
        }
        return output;
    }

    private static Object bytes2type(byte[] bytes, Class type) {
        if (type == short.class|type == Short.class)
            return ByteBuffer.wrap(bytes).getShort();
        else if(type==boolean.class | type==Boolean.class)
            return ByteBuffer.wrap(bytes).getShort()==1;
        else if(type==String.class)
            return new String(bytes);
        return null;
    }
}
