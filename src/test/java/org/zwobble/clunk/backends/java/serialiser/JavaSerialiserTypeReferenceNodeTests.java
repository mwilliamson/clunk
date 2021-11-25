package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.JavaTypeReferenceNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.java.serialiser.JavaSerialiser.serialiseTypeReference;

public class JavaSerialiserTypeReferenceNodeTests {
    @Test
    public void isSerialisedToName() {
        var node = new JavaTypeReferenceNode("Example");

        var builder = new CodeBuilder();
        serialiseTypeReference(node, builder);

        assertThat(builder.toString(), equalTo("Example"));
    }
}
