package org.zwobble.clunk.matchers;

import org.hamcrest.Matcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.TypeLevelValueType;

import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeMatchers {
    public static Matcher<Type> isMetaType(Matcher<Type> value) {
        return cast(
            TypeLevelValueType.class,
            has("value", value)
        );
    }
}
