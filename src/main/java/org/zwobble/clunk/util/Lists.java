package org.zwobble.clunk.util;

import java.util.List;

public class Lists {
    private Lists() {
    }

    public static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }
}
