package org.zwobble.clunk.types;

public class NothingType implements Type {
    public static final NothingType INSTANCE = new NothingType();

    private NothingType() {

    }

    @Override
    public String describe() {
        return "Nothing";
    }

    @Override
    public String identifier() {
        return "Nothing";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
