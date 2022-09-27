package org.zwobble.clunk.types;

import java.util.HashMap;

public class ObjectType implements Type {
    public static final ObjectType INSTANCE = new ObjectType();

    private ObjectType() {
    }

    @Override
    public String describe() {
        return "Object";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
