package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserExpressionPrecedenceTests {
    @Test
    public void subExpressionIsParenthesizedWhenOfLowerPrecedence() {
        var node = TypeScript.call(
            TypeScript.add(TypeScript.reference("a"), TypeScript.reference("b")),
            List.of()
        );

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(a + b)()"));
    }

    @Test
    public void subExpressionIsNotParenthesizedWhenOfSamePrecedence() {
        var node = TypeScript.add(
            TypeScript.add(TypeScript.reference("a"), TypeScript.reference("b")),
            TypeScript.reference("c")
        );

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a + b + c"));
    }

    @Test
    public void subExpressionIsNotParenthesizedWhenOfHigherPrecedence() {
        var node = TypeScript.add(
            TypeScript.call(TypeScript.reference("a"), List.of()),
            TypeScript.reference("b")
        );

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a() + b"));
    }
}
