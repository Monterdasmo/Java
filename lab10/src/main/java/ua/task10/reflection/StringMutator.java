package ua.task10.reflection;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class StringMutator {

    private StringMutator() {}

    /**
     * Mutates the given String object's internal storage to become equal to newValue.
     * Works for:
     *  - old JDKs: char[] value
     *  - modern JDKs: byte[] value + coder (compact strings)
     *
     * On Java 9+ you MUST run with:
     *   --add-opens java.base/java.lang=ALL-UNNAMED
     */
    public static void mutateTo(String target, String newValue) throws Exception {
        if (target == null || newValue == null) throw new IllegalArgumentException("target/newValue cannot be null");

        Field valueField = String.class.getDeclaredField("value");
        valueField.setAccessible(true);

        Object newStorage = valueField.get(newValue);
        Object copiedStorage;

        if (newStorage instanceof byte[] bytes) {
            copiedStorage = Arrays.copyOf(bytes, bytes.length);
            valueField.set(target, copiedStorage);

            // If coder exists, copy it too (Latin1 vs UTF16)
            try {
                Field coderField = String.class.getDeclaredField("coder");
                coderField.setAccessible(true);
                Object coder = coderField.get(newValue);
                coderField.set(target, coder);
            } catch (NoSuchFieldException ignored) {
                // older JDK
            }
        } else if (newStorage instanceof char[] chars) {
            copiedStorage = Arrays.copyOf(chars, chars.length);
            valueField.set(target, copiedStorage);
        } else {
            throw new IllegalStateException("Unknown String.value type: " + newStorage.getClass());
        }

        // Reset cached hash if exists
        try {
            Field hashField = String.class.getDeclaredField("hash");
            hashField.setAccessible(true);
            hashField.setInt(target, 0);
        } catch (NoSuchFieldException ignored) {
            // may differ by JDK
        }
    }
}
