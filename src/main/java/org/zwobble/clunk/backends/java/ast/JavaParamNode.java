package org.zwobble.clunk.backends.java.ast;

public record JavaParamNode(JavaTypeExpressionNode type, String name) implements JavaNode {
}
