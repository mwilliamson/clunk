package org.zwobble.clunk.types;

public class TypeLevelValueTypeSet implements TypeSet {
    public static final TypeLevelValueTypeSet INSTANCE = new TypeLevelValueTypeSet();

    private TypeLevelValueTypeSet() {
    }

    @Override
    public String describe() {
        return "type-level value";
    }
}
