package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserArrayTests {
    @Test
    public void canSerialiseEmptyList() {
        var node = TypeScript.array(List.of());

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[]"));
    }

    @Test
    public void canSerialiseSingletonList() {
        var node = TypeScript.array(List.of(TypeScript.numberLiteral(1)));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[1]"));
    }

    @Test
    public void canSerialiseListWithMultipleElements() {
        var node = TypeScript.array(List.of(
            TypeScript.numberLiteral(1),
            TypeScript.numberLiteral(2),
            TypeScript.numberLiteral(3)
        ));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[1, 2, 3]"));
    }
}
