package org.zwobble.clunk.backends.java.ast;

public interface JavaBinaryOperationNode extends JavaExpressionNode {
    JavaExpressionNode left();
    JavaExpressionNode right();
}
