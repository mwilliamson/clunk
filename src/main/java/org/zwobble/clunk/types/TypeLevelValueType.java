package org.zwobble.clunk.types;

public record TypeLevelValueType(TypeLevelValue value) implements Type {
    @Override
    public String describe() {
        return "TypeLevelValue[" + value.describe() + "]";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        // TODO: implement properly
        return this;
    }
}
