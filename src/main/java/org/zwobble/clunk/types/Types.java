package org.zwobble.clunk.types;

import java.util.List;

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

    public static Type list(Type elementType) {
        return new ListType(elementType);
    }

    public static Type option(Type elementType) {
        return new OptionType(elementType);
    }

    public static TypeLevelValueType metaType(Type type) {
        return new TypeLevelValueType(type);
    }

    public static TypeLevelValueType typeLevelValueType(TypeLevelValue value) {
        return new TypeLevelValueType(value);
    }

    public static TypeLevelValueType typeConstructorType(TypeConstructor typeConstructor) {
        return new TypeLevelValueType(typeConstructor);
    }

    public static Type enumType(NamespaceName namespaceName, String name, List<String> members) {
        return new EnumType(namespaceName, name, members);
    }

    public static InterfaceType interfaceType(NamespaceName namespaceName, String name) {
        return new InterfaceType(namespaceName, name);
    }

    public static RecordType recordType(NamespaceName namespaceName, String name) {
        return new RecordType(namespaceName, name);
    }
}
