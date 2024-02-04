package de.yugata.tts.util;

import java.util.concurrent.ThreadLocalRandom;

public class ArrayUtil {
    /**
     * Private constructor. Prevent instantiation.
     */
    private ArrayUtil() {}

    /**
     *
     * @param values
     * @return
     * @param <E>
     */
    public static <E extends Enum<E>> E getRandomEnumValue(final E[] values) {
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}
