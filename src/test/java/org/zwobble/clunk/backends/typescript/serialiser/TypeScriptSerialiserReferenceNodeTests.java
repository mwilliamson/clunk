package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser.serialiseReference;

public class TypeScriptSerialiserReferenceNodeTests {
    @Test
    public void isSerialisedToName() {
        var node = TypeScript.reference("Example");

        var builder = new CodeBuilder();
        serialiseReference(node, builder);

        assertThat(builder.toString(), equalTo("Example"));
    }
}