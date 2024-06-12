package org.zwobble.clunk.types;

public record TypeArg(TypeParam param) implements Type {
    @Override
    public String describe() {
        return param.name();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
