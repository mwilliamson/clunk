package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StaticFunctionTypeTests {
    @Test
    public void describeIncludesArgsAndReturnType() {
        var type = Types.staticFunctionType(
            NamespaceName.fromParts("A", "B"),
            "f",
            List.of(Types.BOOL, Types.STRING),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("A/B.f: (Bool, String) -> Int"));
    }
}
