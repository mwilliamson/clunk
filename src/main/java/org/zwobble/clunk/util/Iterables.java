package org.zwobble.clunk.util;

import java.util.function.Consumer;

public class Iterables {
    private Iterables() {
    }

    public static <T> void forEachInterspersed(Iterable<T> values, Consumer<T> onElement, Action between) {
        var first = true;
        for (var value : values) {
            if (!first) {
                between.call();
            }
            onElement.accept(value);
            first = false;
        }
    }
}
