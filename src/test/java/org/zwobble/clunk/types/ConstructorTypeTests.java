package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ConstructorTypeTests {
    @Test
    public void describeIncludesArgsAndReturnType() {
        var type = Types.constructorType(
            List.of(Types.BOOL, Types.STRING),
            Types.recordType(NamespaceName.fromParts(), "X")
        );

        var result = type.describe();

        assertThat(result, equalTo("constructor (Bool, String) -> .X"));
    }

    @Test
    public void describeIncludesTypeLevelArgs() {
        var type = Types.constructorType(
            List.of(
                TypeParameter.function(NamespaceName.fromParts(), "T", "f", "A"),
                TypeParameter.function(NamespaceName.fromParts(), "T", "f", "B")
            ),
            List.of(),
            Types.recordType(NamespaceName.fromParts(), "X")
        );

        var result = type.describe();

        assertThat(result, equalTo("constructor [A, B]() -> .X"));
    }

    @Test
    public void replaceReplacesPositionalParamTypes() {
        var typeParameter = TypeParameter.covariant(NamespaceName.fromParts(), "X", "T");
        var type = Types.constructorType(
            List.of(typeParameter),
            Types.recordType(NamespaceName.fromParts(), "X")
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.constructorType(
            List.of(Types.STRING),
            Types.recordType(NamespaceName.fromParts(), "X")
        )));
    }

    @Test
    public void replaceReplacesReturnType() {
        var typeParameter = TypeParameter.covariant(NamespaceName.fromParts(), "X", "T");
        var type = Types.constructorType(
            List.of(Types.INT),
            Types.recordType(NamespaceName.fromParts(), "X")
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.constructorType(
            List.of(Types.INT),
            Types.recordType(NamespaceName.fromParts(), "X")
        )));
    }
}
