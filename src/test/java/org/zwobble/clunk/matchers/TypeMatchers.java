package org.zwobble.clunk.matchers;

import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.TypeLevelValueType;
import org.zwobble.precisely.Matcher;

import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class TypeMatchers {
    public static Matcher<Type> isMetaType(Matcher<Type> value) {
        return instanceOf(
            TypeLevelValueType.class,
            has("value", x -> x.value(), instanceOf(Type.class, value))
        );
    }
}
