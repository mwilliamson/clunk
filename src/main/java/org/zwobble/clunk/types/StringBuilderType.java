package org.zwobble.clunk.types;

import java.util.HashMap;

public class StringBuilderType implements Type {
    public static final StringBuilderType INSTANCE = new StringBuilderType();

    private StringBuilderType() {

    }

    @Override
    public String describe() {
        return "StringBuilder";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
