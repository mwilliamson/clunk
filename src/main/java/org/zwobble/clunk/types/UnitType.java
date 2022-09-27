package org.zwobble.clunk.types;

import java.util.HashMap;

public class UnitType implements Type {
    public static final UnitType INSTANCE = new UnitType();

    private UnitType() {

    }

    @Override
    public String describe() {
        return "Unit";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
