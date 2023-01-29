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
    public static final RecordType STRING_BUILDER = recordType(NamespaceName.fromParts(), "StringBuilder");
    public static final Type UNIT = UnitType.INSTANCE;

    public static final TypeConstructor LIST_CONSTRUCTOR = new TypeConstructor(
        List.of(TypeParameter.covariant(NamespaceName.fromParts(), "List", "T")),
        Types.interfaceType(NamespaceName.fromParts(), "List")
    );

    public static final TypeConstructor MAP_CONSTRUCTOR = new TypeConstructor(
        List.of(
            TypeParameter.covariant(NamespaceName.fromParts(), "Map", "K"),
            TypeParameter.covariant(NamespaceName.fromParts(), "Map", "V")
        ),
        Types.interfaceType(NamespaceName.fromParts(), "Map")
    );

    public static final TypeConstructor MUTABLE_LIST_CONSTRUCTOR = new TypeConstructor(
        List.of(TypeParameter.invariant(NamespaceName.fromParts(), "MutableList", "T")),
        Types.recordType(NamespaceName.fromParts(), "MutableList")
    );

    public static final TypeConstructor OPTION_CONSTRUCTOR = new TypeConstructor(
        List.of(TypeParameter.covariant(NamespaceName.fromParts(), "Option", "T")),
        Types.recordType(NamespaceName.fromParts(), "Option")
    );

    public static StructuredType list(Type elementType) {
        return construct(LIST_CONSTRUCTOR, List.of(elementType));
    }

    public static StructuredType map(Type keyType, Type valueType) {
        return construct(MAP_CONSTRUCTOR, List.of(keyType, valueType));
    }

    public static StructuredType mutableList(Type elementType) {
        return construct(MUTABLE_LIST_CONSTRUCTOR, List.of(elementType));
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
        return new InterfaceType(namespaceName, name, false);
    }

    public static InterfaceType unsealedInterfaceType(NamespaceName namespaceName, String name) {
        return new InterfaceType(namespaceName, name, false);
    }

    public static ConstructorType constructorType(
        List<TypeParameter> typeLevelParams,
        List<Type> positionalParams,
        StructuredType returnType
    ) {
        return new ConstructorType(
            returnType.namespaceName(),
            Optional.of(typeLevelParams),
            positionalParams,
            returnType,
            Visibility.PUBLIC
        );
    }

    public static ConstructorType constructorType(
        List<TypeParameter> typeLevelParams,
        List<Type> positionalParams,
        StructuredType returnType,
        Visibility visibility
    ) {
        return new ConstructorType(
            returnType.namespaceName(),
            Optional.of(typeLevelParams),
            positionalParams,
            returnType,
            visibility
        );
    }

    public static ConstructorType constructorType(
        List<Type> positionalParams,
        StructuredType returnType
    ) {
        return new ConstructorType(
            returnType.namespaceName(),
            Optional.empty(),
            positionalParams,
            returnType,
            Visibility.PUBLIC
        );
    }

    public static ConstructorType constructorType(
        List<Type> positionalParams,
        StructuredType returnType,
        Visibility visibility
    ) {
        return new ConstructorType(
            returnType.namespaceName(),
            Optional.empty(),
            positionalParams,
            returnType,
            visibility
        );
    }

    public static MethodType methodType(NamespaceName namespaceName, List<TypeParameter> typeLevelParams, List<Type> positionalParams, Type returnType) {
        return new MethodType(namespaceName, Optional.of(typeLevelParams), positionalParams, returnType, Visibility.PUBLIC);
    }

    public static MethodType methodType(StructuredType type, List<TypeParameter> typeLevelParams, List<Type> positionalParams, Type returnType) {
        return methodType(type.namespaceName(), typeLevelParams, positionalParams, returnType);
    }

    public static MethodType methodType(TypeConstructor typeConstructor, List<TypeParameter> typeLevelParams, List<Type> positionalParams, Type returnType) {
        return methodType(typeConstructor.genericType(), typeLevelParams, positionalParams, returnType);
    }

    public static MethodType methodType(NamespaceName namespaceName, List<Type> positionalParams, Type returnType) {
        return new MethodType(namespaceName, Optional.empty(), positionalParams, returnType, Visibility.PUBLIC);
    }

    public static MethodType methodType(StructuredType type, List<Type> positionalParams, Type returnType) {
        return methodType(type.namespaceName(), positionalParams, returnType);
    }

    public static MethodType methodType(TypeConstructor typeConstructor, List<Type> positionalParams, Type returnType) {
        return methodType(typeConstructor.genericType(), positionalParams, returnType);
    }

    public static StaticFunctionType staticFunctionType(
        NamespaceName namespaceName,
        String functionName,
        List<Type> positionalParams,
        Type returnType
    ) {
        return new StaticFunctionType(namespaceName, functionName, positionalParams, returnType, Visibility.PUBLIC);
    }

    public static RecordType recordType(NamespaceName namespaceName, String name) {
        return new RecordType(namespaceName, name);
    }

    public static InterfaceType sealedInterfaceType(NamespaceName namespaceName, String name) {
        return new InterfaceType(namespaceName, name, true);
    }

    public static boolean isSealedInterfaceType(Type type) {
        return type instanceof InterfaceType;
    }

    public static StructuredType construct(TypeConstructor constructor, List<? extends Type> args) {
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
