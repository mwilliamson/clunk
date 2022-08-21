package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptBinaryOperationNode extends TypeScriptExpressionNode {
    TypeScriptExpressionNode left();
    TypeScriptExpressionNode right();
}
