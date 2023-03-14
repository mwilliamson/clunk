package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeUnifierTests {
    @Test
    public void unificationOfTypeWithItselfIsSameType() {
        var result = TypeUnifier.unify(Types.INT, Types.INT);

        assertThat(result, equalTo(Types.INT));
    }

    @Test
    public void unificationOfTypesWithoutCommonSupertypeIsObject() {
        var result = TypeUnifier.unify(Types.INT, Types.STRING);

        assertThat(result, equalTo(Types.OBJECT));
    }

    @Test
    public void unificationOfNothingWithOtherTypeIsOtherType() {
        var result = TypeUnifier.unify(Types.NOTHING, Types.STRING);

        assertThat(result, equalTo(Types.STRING));
    }

    @Test
    public void unificationOfOtherTypeWithNothingIsOtherType() {
        var result = TypeUnifier.unify(Types.STRING, Types.NOTHING);

        assertThat(result, equalTo(Types.STRING));
    }
}
