package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptAddNode(TypeScriptExpressionNode left, TypeScriptExpressionNode right) implements TypeScriptExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
