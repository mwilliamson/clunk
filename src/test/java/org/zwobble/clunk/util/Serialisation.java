package org.zwobble.clunk.util;

import java.util.function.BiConsumer;

public class Serialisation {
    private Serialisation() {

    }

    public static <T> String serialiseToString(T value, BiConsumer<T, StringBuilder> serialise) {
        var builder = new StringBuilder();
        serialise.accept(value, builder);
        return builder.toString();
    }
}
