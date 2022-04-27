package org.zwobble.clunk.types;

public class Types {
    private Types() {

    }

    public static final Type BOOL = BoolType.INSTANCE;
    public static final Type INT = IntType.INSTANCE;
    public static final Type OBJECT = ObjectType.INSTANCE;
    public static final Type STRING = StringType.INSTANCE;
    public static final Type UNIT = UnitType.INSTANCE;

    public static boolean isSubType(Type subType, Type superType) {
        if (superType == OBJECT) {
            return true;
        }

        return subType.equals(superType);
    }

    public static MetaType metaType(Type type) {
        return new MetaType(type);
    }
}
