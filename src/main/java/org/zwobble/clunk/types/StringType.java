package org.zwobble.clunk.types;

public class StringType implements Type {
    public static final StringType INSTANCE = new StringType();

    private StringType() {

    }

    @Override
    public String describe() {
        return "String";
    }

    @Override
    public String identifier() {
        return "String";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
