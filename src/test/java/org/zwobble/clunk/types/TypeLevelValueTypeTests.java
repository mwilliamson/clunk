package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeLevelValueTypeTests {
    @Test
    public void describeIncludesType() {
        var type = Types.metaType(Types.INT);

        var result = type.describe();

        assertThat(result, equalTo("TypeLevelValue[Int]"));
    }
}
