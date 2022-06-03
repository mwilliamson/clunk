package org.zwobble.clunk.backends.java.ast;

public record JavaImportStaticNode(String typeName, String identifier) implements JavaImportNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
