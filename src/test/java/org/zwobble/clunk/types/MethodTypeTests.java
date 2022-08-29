package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MethodTypeTests {
    @Test
    public void describeIncludesArgsAndReturnType() {
        var type = new MethodType(
            List.of(Types.BOOL, Types.STRING),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("(Bool, String) -> Int"));
    }
}
