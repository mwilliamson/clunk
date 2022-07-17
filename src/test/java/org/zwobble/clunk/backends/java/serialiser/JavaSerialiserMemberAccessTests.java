package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserMemberAccessTests {
    @Test
    public void canSerialiseMemberAccess() {
        var node = Java.memberAccess(Java.reference("x"), "y");

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("(x).y"));
    }
}
