package org.zwobble.clunk.backends.java.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.JavaExpressionNode;

import java.util.Optional;

public class JavaSerialiserTesting {
    public static void serialiseExpression(JavaExpressionNode node, CodeBuilder builder) {
        JavaSerialiser.serialiseExpression(node, builder, Optional.empty());
    }
}
