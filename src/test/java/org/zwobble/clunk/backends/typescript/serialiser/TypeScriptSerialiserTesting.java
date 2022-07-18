package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;

import java.util.Optional;

public class TypeScriptSerialiserTesting {
    public static void serialiseExpression(TypeScriptExpressionNode node, CodeBuilder builder) {
        TypeScriptSerialiser.serialiseExpression(node, builder, Optional.empty());
    }
}
