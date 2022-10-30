package org.zwobble.clunk.types;

public class BoolType implements Type {
    public static final BoolType INSTANCE = new BoolType();

    private BoolType() {

    }

    @Override
    public String describe() {
        return "Bool";
    }

    @Override
    public String identifier() {
        return "Bool";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
