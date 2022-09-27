package org.zwobble.clunk.types;

import java.util.HashMap;

public class IntType implements Type {
    public static final IntType INSTANCE = new IntType();

    private IntType() {

    }

    @Override
    public String describe() {
        return "Int";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
