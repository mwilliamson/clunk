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
}
