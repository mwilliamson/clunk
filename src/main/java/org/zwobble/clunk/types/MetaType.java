package org.zwobble.clunk.types;

public record MetaType(Type type) implements Type {
    @Override
    public String describe() {
        return "MetaType[" + type.describe() + "]";
    }
}
