package org.zwobble.clunk.types;

public record MetaType(TypeLevelValue value) implements Type {
    @Override
    public String describe() {
        return "MetaType[" + value.describe() + "]";
    }
}
