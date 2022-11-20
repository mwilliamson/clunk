package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;

public class Types {
    private Types() {

    }

    public static final Type BOOL = BoolType.INSTANCE;
    public static final TypeSet CALLABLE = CallableTypeSet.INSTANCE;
    public static final Type INT = IntType.INSTANCE;
    public static final Type NOTHING = NothingType.INSTANCE;
    public static final Type OBJECT = ObjectType.INSTANCE;
    public static final Type STRING = StringType.INSTANCE;
    public static final Type STRING_BUILDER = StringBuilderType.INSTANCE;
    public static final Type UNIT = UnitType.INSTANCE;

    public static final TypeConstructor LIST_CONSTRUCTOR = new TypeConstructor(
        "List",
        List.of(TypeParameter.covariant(NamespaceName.fromParts(), "List", "T")),
        Types.recordType(NamespaceName.fromParts(), "List", Visibility.PRIVATE)
    );

    public static final TypeConstructor MUTABLE_LIST_CONSTRUCTOR = new TypeConstructor(
        "MutableList",
        List.of(TypeParameter.invariant(NamespaceName.fromParts(), "MutableList", "T")),
        Types.recordType(NamespaceName.fromParts(), "MutableList", Visibility.PUBLIC)
    );

    public static final TypeConstructor OPTION_CONSTRUCTOR = new TypeConstructor(
        "Option",
        List.of(TypeParameter.covariant(NamespaceName.fromParts(), "Option", "T")),
        Types.recordType(NamespaceName.fromParts(), "Option", Visibility.PRIVATE)
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

    public static FunctionType functionType(List<Type> positionalParams, Type returnType) {
        return new FunctionType(positionalParams, returnType);
    }

    public static InterfaceType interfaceType(NamespaceName namespaceName, String name) {
        return new InterfaceType(namespaceName, name);
    }

    public static MethodType methodType(List<TypeParameter> typeLevelParams, List<Type> positionalParams, Type returnType) {
        return new MethodType(Optional.of(typeLevelParams), positionalParams, returnType);
    }

    public static MethodType methodType(List<Type> positionalParams, Type returnType) {
        return new MethodType(Optional.empty(), positionalParams, returnType);
    }

    public static RecordType recordType(
        NamespaceName namespaceName,
        String name,
        Visibility constructorVisibility
    ) {
        return new RecordType(namespaceName, name, constructorVisibility);
    }

    public static RecordType recordType(NamespaceName namespaceName, String name) {
        return recordType(namespaceName, name, Visibility.PUBLIC);
    }

    public static InterfaceType sealedInterfaceType(NamespaceName namespaceName, String name) {
        return new InterfaceType(namespaceName, name);
    }

    public static boolean isSealedInterfaceType(Type type) {
        return type instanceof InterfaceType;
    }

    public static StructuredType construct(TypeConstructor constructor, List<Type> args) {
        // TODO: check args
        return new ConstructedType(constructor, args);
    }

    public static Type unify(Type left, Type right) {
        return TypeUnifier.unify(left, right);
    }

    public static Type unify(List<Type> types, Type defaultType) {
        return types.stream()
            .reduce((x, y) -> unify(x, y))
            .orElse(defaultType);
    }
}
