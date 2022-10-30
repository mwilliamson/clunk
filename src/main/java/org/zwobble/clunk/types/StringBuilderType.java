package org.zwobble.clunk.types;

public class StringBuilderType implements Type {
    public static final StringBuilderType INSTANCE = new StringBuilderType();

    private StringBuilderType() {

    }

    @Override
    public String describe() {
        return "StringBuilder";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
