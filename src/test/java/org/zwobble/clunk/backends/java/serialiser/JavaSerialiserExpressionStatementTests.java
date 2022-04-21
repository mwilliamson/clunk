package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserExpressionStatementTests {
    @Test
    public void expressionIsSerialisedAsStatement() {
        var node = Java.expressionStatement(Java.boolFalse());

        var result = serialiseToString(node, JavaSerialiser::serialiseStatement);

        assertThat(result, equalTo("false;\n"));
    }
}
