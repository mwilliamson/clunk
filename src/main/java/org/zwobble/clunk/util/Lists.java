package org.zwobble.clunk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Lists {
    private Lists() {
    }

    public static <T> List<T> concatOne(T element, List<T> list) {
        var result = new ArrayList<T>();
        result.add(element);
        result.addAll(list);
        return result;
    }

    public static <T> List<T> concatOne(List<T> list, T element) {
        var result = new ArrayList<>(list);
        result.add(element);
        return result;
    }

    public static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> List<T> updateLast(List<T> list, Function<T, T> func) {
        var result = new ArrayList<>(list);
        var lastIndex = list.size() - 1;
        result.set(lastIndex, func.apply(list.get(lastIndex)));
        return result;
    }
}
