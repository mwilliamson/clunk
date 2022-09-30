package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserForEachTests {
    @Test
    public void canSerialiseForEach() {
        var node = Java.forEach(
            "x",
            Java.reference("xs"),
            List.of(
                Java.expressionStatement(Java.callStatic(
                    Java.typeVariableReference("println"),
                    List.of(Java.reference("x"))
                ))
            )
        );

        var result = serialiseToString(node, JavaSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            for (var x : xs) {
                println(x);
            }
            """));
    }
}
