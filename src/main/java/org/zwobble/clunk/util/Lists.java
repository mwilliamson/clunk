package org.zwobble.clunk.util;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    private Lists() {
    }

    public static <T> List<T> concatOne(List<T> list, T element) {
        var result = new ArrayList<>(list);
        result.add(element);
        return result;
    }

    public static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }
}
