package org.zwobble.clunk.types;

import java.util.HashMap;

public class BoolType implements Type {
    public static final BoolType INSTANCE = new BoolType();

    private BoolType() {

    }

    @Override
    public String describe() {
        return "Bool";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
