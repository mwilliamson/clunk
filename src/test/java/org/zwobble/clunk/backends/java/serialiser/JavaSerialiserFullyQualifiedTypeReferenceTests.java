package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.JavaFullyQualifiedTypeReferenceNode;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserFullyQualifiedTypeReferenceTests {
    @Test
    public void isSerialisedToPackageAndName() {
        var node = new JavaFullyQualifiedTypeReferenceNode("one.two.three", "Example");

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeExpression);

        assertThat(result, equalTo("one.two.three.Example"));
    }
}
