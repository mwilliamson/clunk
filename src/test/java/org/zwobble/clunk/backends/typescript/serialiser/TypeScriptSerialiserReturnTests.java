package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserReturnTests {
    @Test
    public void returnIsSerialisedWithExpression() {
        var node = TypeScript.returnStatement(TypeScript.boolFalse());

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("return false;\n"));
    }
}
