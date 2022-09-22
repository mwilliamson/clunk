package org.zwobble.clunk.matchers;

import org.hamcrest.Matcher;

import java.util.Map;

import static org.hamcrest.Matchers.hasProperty;
import static org.zwobble.clunk.matchers.CastMatcher.cast;

public class MapEntryMatcher {
    public static <K, V> Matcher<Map.Entry<K, V>> isMapEntry(
        Matcher<K> key,
        Matcher<V> value
    ) {
        return cast(
            Map.Entry.class,
            hasProperty("key", key),
            hasProperty("value", value)
        );
    }
}
