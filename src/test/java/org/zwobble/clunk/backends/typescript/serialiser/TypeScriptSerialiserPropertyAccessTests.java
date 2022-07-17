package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserPropertyAccessTests {
    @Test
    public void canSerialisePropertyAccess() {
        var node = TypeScript.propertyAccess(TypeScript.reference("x"), "y");

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseExpression);

        assertThat(result, equalTo("(x).y"));
    }
}
