package org.zwobble.clunk.types;

import java.util.List;

public class Types {
    private Types() {

    }

    public static final Type BOOL = BoolType.INSTANCE;
    public static final Type INT = IntType.INSTANCE;
    public static final Type NOTHING = NothingType.INSTANCE;
    public static final Type OBJECT = ObjectType.INSTANCE;
    public static final Type STRING = StringType.INSTANCE;
    public static final Type STRING_BUILDER = StringBuilderType.INSTANCE;
    public static final Type UNIT = UnitType.INSTANCE;

    public static final TypeConstructor LIST_CONSTRUCTOR = new TypeConstructor(
        "List",
        List.of(TypeParameter.covariant("T"))
    );

    public static final TypeConstructor OPTION_CONSTRUCTOR = new TypeConstructor(
        "Option",
        List.of(TypeParameter.covariant("T"))
    );

    public static Type list(Type elementType) {
        return construct(LIST_CONSTRUCTOR, List.of(elementType));
    }

    public static Type option(Type elementType) {
        return construct(OPTION_CONSTRUCTOR, List.of(elementType));
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

    public static MethodType methodType(List<Type> positionalParams, Type returnType) {
        return new MethodType(positionalParams, returnType);
    }

    public static RecordType recordType(NamespaceName namespaceName, String name) {
        return new RecordType(namespaceName, name);
    }

    public static InterfaceType sealedInterfaceType(NamespaceName namespaceName, String name) {
        return new InterfaceType(namespaceName, name);
    }

    public static boolean isSealedInterfaceType(Type type) {
        return type instanceof InterfaceType;
    }

    public static Type construct(TypeConstructor constructor, List<Type> args) {
        // TODO: check args
        return new ConstructedType(constructor, args);
    }

    public static Type unify(Type left, Type right) {
        return TypeUnifier.unify(left, right);
    }

    public static Type unify(List<Type> types) {
        return types.stream()
            .reduce((x, y) -> unify(x, y))
            .orElse(Types.OBJECT);
    }
}
