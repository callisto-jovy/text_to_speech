package de.yugata.tts.util;

import java.util.concurrent.ThreadLocalRandom;

public class ArrayUtil {
    /**
     * Private constructor. Prevent instantiation.
     */
    private ArrayUtil() {
    }

    /**
     * Grabs a random value using {@link ThreadLocalRandom} from the given enum value array.
     *
     * @param values the enum values as an array to grab a random value.
     * @param <E>    extends a Enum
     * @return random value from the supplied array.
     */
    public static <E extends Enum<E>> E getRandomEnumValue(final E[] values) {
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}
