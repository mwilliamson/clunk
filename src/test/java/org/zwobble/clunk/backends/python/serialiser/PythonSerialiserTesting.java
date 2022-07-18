package org.zwobble.clunk.backends.python.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;

import java.util.Optional;

public class PythonSerialiserTesting {
    public static void serialiseExpression(PythonExpressionNode node, CodeBuilder builder) {
        PythonSerialiser.serialiseExpression(node, builder, Optional.empty());
    }
}
