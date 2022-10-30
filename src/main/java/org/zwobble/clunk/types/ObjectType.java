package org.zwobble.clunk.types;

public class ObjectType implements Type {
    public static final ObjectType INSTANCE = new ObjectType();

    private ObjectType() {
    }

    @Override
    public String describe() {
        return "Object";
    }

    @Override
    public String identifier() {
        return "Object";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
