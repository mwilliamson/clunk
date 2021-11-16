package org.zwobble.clunk.ast;

import org.zwobble.clunk.sources.Source;

public record StaticReferenceNode(String value, Source source) implements StaticExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
