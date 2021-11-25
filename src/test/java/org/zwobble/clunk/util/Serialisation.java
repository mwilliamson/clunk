package org.zwobble.clunk.util;

import org.zwobble.clunk.backends.CodeBuilder;

import java.util.function.BiConsumer;

public class Serialisation {
    private Serialisation() {

    }

    public static <T> String serialiseToString(T value, BiConsumer<T, CodeBuilder> serialise) {
        var builder = new CodeBuilder();
        serialise.accept(value, builder);
        return builder.toString();
    }
}
