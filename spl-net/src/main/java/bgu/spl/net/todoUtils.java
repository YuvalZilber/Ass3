package bgu.spl.net;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class todoUtils {

    /**
     * to check if this works on several classes
     *
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void putField(Object obj, String fieldName, Object value) {
        Class<?> type = obj.getClass();
        try {
            Field f = type.getDeclaredField(fieldName);
            f.set(obj, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param type
     * @param fields
     * @param <T>
     * @return a new object of class {@param T} with the fields named {@code fields.getKeys()} with the values in {@code fields.values}
     */
    public abstract <T> T getObject(Class<T> type, String[] fieldsNames, Object[] fieldsValues);

    /**
     * @param bytes        input of bytes from stream
     * @param splitIndexes array of indexes that declare new value
     * @param types        array of values type
     * @return values that encoded in bytes
     */
    public abstract Object[] getValues(byte[] bytes, int[] splitIndexes, Class<?>[] types);
}
