package org.zwobble.clunk.backends.java.ast;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.java.ast.JavaSerialiser.serialiseTypeReference;

public class JavaTypeReferenceNodeSerialisationTests {
    @Test
    public void isSerialisedToName() {
        var node = new JavaTypeReferenceNode("Example");

        var stringBuilder = new StringBuilder();
        serialiseTypeReference(node, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo("Example"));
    }
}
