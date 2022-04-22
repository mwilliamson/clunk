package org.zwobble.clunk.types;

public class BoolType implements Type {
    public static final BoolType INSTANCE = new BoolType();

    private BoolType() {

    }

    @Override
    public String describe() {
        return "Bool";
    }
}
