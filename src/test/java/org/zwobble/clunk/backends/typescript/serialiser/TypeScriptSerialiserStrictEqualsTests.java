package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserStrictEqualsTests {
    @Test
    public void canSerialiseStrictEquals() {
        var node = TypeScript.strictEquals(
            TypeScript.reference("a"),
            TypeScript.reference("b")
        );

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a === b"));
    }

}
