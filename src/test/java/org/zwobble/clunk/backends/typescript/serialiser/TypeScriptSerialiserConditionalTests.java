package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptSerialiserConditionalTests {
    @Test
    public void canSerialiseConditionalOperator() {
        var node = TypeScript.conditional(
            TypeScript.reference("a"),
            TypeScript.reference("b"),
            TypeScript.reference("c")
        );

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a ? b : c"));
    }
}
