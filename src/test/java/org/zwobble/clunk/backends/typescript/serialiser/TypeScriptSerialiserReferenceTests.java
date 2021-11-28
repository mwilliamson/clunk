package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserReferenceTests {
    @Test
    public void isSerialisedToName() {
        var node = TypeScript.reference("Example");

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseReference);

        assertThat(result, equalTo("Example"));
    }
}
