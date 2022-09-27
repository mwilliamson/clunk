package org.zwobble.clunk.types;

public class IntType implements Type {
    public static final IntType INSTANCE = new IntType();

    private IntType() {

    }

    @Override
    public String describe() {
        return "Int";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
