package org.zwobble.clunk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Maps {
    public Maps() {
    }

    public static <K, V1, V2> Map<K, V2> mapValues(Map<K, V1> map, Function<V1, V2> func) {
        var result = new HashMap<K, V2>(map.size());
        for (var entry : map.entrySet()) {
            result.put(entry.getKey(), func.apply(entry.getValue()));
        }
        return result;
    }
}
