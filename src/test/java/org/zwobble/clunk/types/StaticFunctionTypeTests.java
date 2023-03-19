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

    @Test
    public void describeIncludesTypeLevelArgs() {
        var type = Types.staticFunctionType(
            NamespaceId.source("A", "B"),
            "f",
            List.of(
                TypeParameter.function(NamespaceId.source(), "f", "T"),
                TypeParameter.function(NamespaceId.source(), "f", "U")
            ),
            ParamTypes.empty(),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("A/B.f: [T, U]() -> Int"));
    }
}
