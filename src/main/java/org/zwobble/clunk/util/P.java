package org.zwobble.clunk.util;

import org.pcollections.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class P {
    private P() {
    }

    @SafeVarargs
    public static <K, V> PMap<K, V> map(Map.Entry<K, V>... entries) {
        var map = HashTreePMap.<K, V>empty();
        for (var entry : entries) {
            map = map.plus(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @SafeVarargs
    public static <T> PStack<T> stack(T... values) {
        return ConsPStack.from(Arrays.asList(values));
    }

    public static <T> PStack<T> stackUpdateTop(PStack<T> stack, Function<T, T> func) {
        return stack.with(0, func.apply(stack.get(0)));
    }

    @SafeVarargs
    public static <T> PVector<T> vector(T... values) {
        return TreePVector.from(Arrays.asList(values));
    }
}
