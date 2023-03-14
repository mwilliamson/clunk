package org.zwobble.clunk.matchers;

import org.zwobble.precisely.Matcher;

import java.util.Map;

import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class MapEntryMatcher {
    public static <K, V> Matcher<Map.Entry<K, V>> isMapEntry(
        Matcher<? super K> key,
        Matcher<? super V> value
    ) {
        return instanceOf(
            Map.Entry.class,
            has("key", x -> (K)x.getKey(), key),
            has("value", x -> (V)x.getValue(), value)
        );
    }
}
