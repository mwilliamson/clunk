package org.zwobble.clunk.types;

import java.util.HashMap;

public class NothingType implements Type {
    public static final NothingType INSTANCE = new NothingType();

    private NothingType() {

    }

    @Override
    public String describe() {
        return "Nothing";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
