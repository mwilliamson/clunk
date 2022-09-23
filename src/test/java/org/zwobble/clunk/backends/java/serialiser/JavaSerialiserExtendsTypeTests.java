package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserExtendsTypeTests {
    @Test
    public void isSerialisedToQuestionMark() {
        var node = Java.extendsType(Java.wildcardType(), Java.typeVariableReference("Node"));

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeExpression);

        assertThat(result, equalTo("? extends Node"));
    }
}
