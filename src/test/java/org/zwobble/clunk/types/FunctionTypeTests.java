package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FunctionTypeTests {
    @Test
    public void describeIncludesArgsAndReturnType() {
        var type = Types.functionType(
            List.of(Types.BOOL, Types.STRING),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("fn (Bool, String) -> Int"));
    }

    @Test
    public void replaceReplacesPositionalParamTypes() {
        var typeParameter = TypeParameter.covariant(NamespaceName.fromParts(), "X", "T");
        var type = Types.functionType(
            List.of(typeParameter),
            Types.INT
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.functionType(
            List.of(Types.STRING),
            Types.INT
        )));
    }

    @Test
    public void replaceReplacesReturnType() {
        var typeParameter = TypeParameter.covariant(NamespaceName.fromParts(), "X", "T");
        var type = Types.functionType(
            List.of(Types.INT),
            typeParameter
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.functionType(
            List.of(Types.INT),
            Types.STRING
        )));
    }
}
