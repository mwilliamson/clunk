package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class StaticFunctionTypeTests {
    @Test
    public void describeIncludesArgsAndReturnType() {
        var type = Types.staticFunctionType(
            NamespaceId.source("A", "B"),
            "f",
            List.of(Types.BOOL, Types.STRING),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("A/B.f: (Bool, String) -> Int"));
    }
}
