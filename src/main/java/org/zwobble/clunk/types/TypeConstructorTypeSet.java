package org.zwobble.clunk.types;

public class TypeConstructorTypeSet implements TypeSet {
    public static final TypeConstructorTypeSet INSTANCE = new TypeConstructorTypeSet();

    private TypeConstructorTypeSet() {
    }

    @Override
    public String describe() {
        return "type constructor";
    }
}
