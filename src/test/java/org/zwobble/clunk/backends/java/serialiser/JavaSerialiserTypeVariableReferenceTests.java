package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.JavaTypeVariableReferenceNode;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserTypeVariableReferenceTests {
    @Test
    public void isSerialisedToName() {
        var node = new JavaTypeVariableReferenceNode("Example");

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeExpression);

        assertThat(result, equalTo("Example"));
    }
}
