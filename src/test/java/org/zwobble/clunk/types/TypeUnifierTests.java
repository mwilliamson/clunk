package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
}
