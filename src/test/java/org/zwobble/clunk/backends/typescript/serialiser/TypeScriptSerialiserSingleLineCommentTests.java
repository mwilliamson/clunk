package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserSingleLineCommentTests {
    @Test
    public void canSerialiseExpressionStatement() {
        var node = TypeScript.singleLineComment(" Beware.");

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("// Beware.\n"));
    }
}
