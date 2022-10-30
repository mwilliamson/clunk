package org.zwobble.clunk.types;

public class UnitType implements Type {
    public static final UnitType INSTANCE = new UnitType();

    private UnitType() {

    }

    @Override
    public String describe() {
        return "Unit";
    }

    @Override
    public String identifier() {
        return "Unit";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
