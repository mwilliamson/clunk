package org.zwobble.clunk.types;

public class UnitType implements Type {
    public static final UnitType INSTANCE = new UnitType();

    private UnitType() {

    }

    @Override
    public String describe() {
        return "Unit";
    }
}
