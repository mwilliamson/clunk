package org.zwobble.clunk.types;

import java.util.HashMap;

public class StringType implements Type {
    public static final StringType INSTANCE = new StringType();

    private StringType() {

    }

    @Override
    public String describe() {
        return "String";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
