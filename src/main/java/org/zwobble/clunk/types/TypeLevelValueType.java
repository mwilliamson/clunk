package org.zwobble.clunk.types;

public record TypeLevelValueType(TypeLevelValue value) implements Type {
    @Override
    public String describe() {
        return "TypeLevelValue[" + value.describe() + "]";
    }

    @Override
    public String identifier() {
        return "TypeLevelValue";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        // TODO: implement properly
        return this;
    }
}
