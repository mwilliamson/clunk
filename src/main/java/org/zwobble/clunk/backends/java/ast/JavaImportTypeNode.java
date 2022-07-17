package org.zwobble.clunk.backends.java.ast;

public record JavaImportTypeNode(String typeName) implements JavaImportNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
