package org.zwobble.clunk.util;

import java.util.ArrayList;
import java.util.List;
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

    public static <T> List<Pair<T, T>> slidingPairs(Iterable<T> values) {
        var iterator = values.iterator();
        if (!iterator.hasNext()) {
            return List.of();
        }

        var result = new ArrayList<Pair<T, T>>();

        var previous = iterator.next();
        while (iterator.hasNext()) {
            var current = iterator.next();
            result.add(new Pair<>(previous, current));
            previous = current;
        }

        return result;
    }
}
