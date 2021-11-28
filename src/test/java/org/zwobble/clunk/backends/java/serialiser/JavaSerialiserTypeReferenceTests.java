package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.JavaTypeReferenceNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserTypeReferenceTests {
    @Test
    public void isSerialisedToName() {
        var node = new JavaTypeReferenceNode("Example");

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeReference);

        assertThat(result, equalTo("Example"));
    }
}
