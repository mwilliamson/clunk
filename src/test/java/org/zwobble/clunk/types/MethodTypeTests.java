package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class MethodTypeTests {
    @Test
    public void describeIncludesArgsAndReturnType() {
        var type = Types.methodType(
            NamespaceId.source(),
            List.of(Types.BOOL, Types.STRING),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("method (Bool, String) -> Int"));
    }

    @Test
    public void describeIncludesTypeLevelArgs() {
        var type = Types.methodType(
            NamespaceId.source(),
            List.of(
                TypeParameter.method(NamespaceId.source(), "T", "f", "A"),
                TypeParameter.method(NamespaceId.source(), "T", "f", "B")
            ),
            List.of(),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("method [A, B]() -> Int"));
    }

    @Test
    public void replaceReplacesPositionalParamTypes() {
        var typeParameter = TypeParameter.covariant(NamespaceId.source(), "X", "T");
        var type = Types.methodType(
            NamespaceId.source(),
            List.of(typeParameter),
            Types.INT
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.methodType(
            NamespaceId.source(),
            List.of(Types.STRING),
            Types.INT
        )));
    }

    @Test
    public void replaceReplacesReturnType() {
        var typeParameter = TypeParameter.covariant(NamespaceId.source(), "X", "T");
        var type = Types.methodType(
            NamespaceId.source(),
            List.of(Types.INT),
            typeParameter
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.methodType(
            NamespaceId.source(),
            List.of(Types.INT),
            Types.STRING
        )));
    }
}
