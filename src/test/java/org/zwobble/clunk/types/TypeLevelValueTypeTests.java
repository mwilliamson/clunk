package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TypeLevelValueTypeTests {
    @Test
    public void describeIncludesType() {
        var type = Types.metaType(Types.INT);

        var result = type.describe();

        assertThat(result, equalTo("TypeLevelValue[Int]"));
    }
}
