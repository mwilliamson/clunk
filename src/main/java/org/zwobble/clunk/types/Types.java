package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;

public class Types {
    private Types() {

    }

    public static final NamespaceId BUILTIN_NAMESPACE_ID = NamespaceId.source();
    public static final Type BOOL = BoolType.INSTANCE;
    public static final TypeSet CALLABLE = CallableTypeSet.INSTANCE;
    public static final Type INT = IntType.INSTANCE;
    public static final Type NOTHING = NothingType.INSTANCE;
    public static final Type OBJECT = ObjectType.INSTANCE;
    public static final Type STRING = StringType.INSTANCE;
    public static final RecordType STRING_BUILDER = recordType(BUILTIN_NAMESPACE_ID, "StringBuilder");
    public static final Type UNIT = UnitType.INSTANCE;

    public static final TypeConstructor LIST_CONSTRUCTOR = new TypeConstructor(
        List.of(TypeParameter.covariant(BUILTIN_NAMESPACE_ID, "List", "T")),
        Types.interfaceType(BUILTIN_NAMESPACE_ID, "List")
    );

    public static final TypeConstructor MAP_CONSTRUCTOR = new TypeConstructor(
        List.of(
            TypeParameter.covariant(NamespaceId.source(), "Map", "K"),
            TypeParameter.covariant(NamespaceId.source(), "Map", "V")
        ),
        Types.interfaceType(BUILTIN_NAMESPACE_ID, "Map")
    );

    public static final TypeConstructor MEMBER_CONSTRUCTOR = new TypeConstructor(
        List.of(
            TypeParameter.contravariant(BUILTIN_NAMESPACE_ID, "Member", "T"),
            TypeParameter.covariant(BUILTIN_NAMESPACE_ID, "Member", "R")
        ),
        Types.recordType(BUILTIN_NAMESPACE_ID, "Member")
    );

    public static final TypeConstructor MUTABLE_LIST_CONSTRUCTOR = new TypeConstructor(
        List.of(TypeParameter.invariant(BUILTIN_NAMESPACE_ID, "MutableList", "T")),
        Types.recordType(BUILTIN_NAMESPACE_ID, "MutableList")
    );

    public static final TypeConstructor OPTION_CONSTRUCTOR = new TypeConstructor(
        List.of(TypeParameter.covariant(BUILTIN_NAMESPACE_ID, "Option", "T")),
        Types.recordType(BUILTIN_NAMESPACE_ID, "Option")
    );

    public static StructuredType list(Type elementType) {
        return construct(LIST_CONSTRUCTOR, List.of(elementType));
    }

    public static Optional<Type> listElementType(Type type) {
        if (
            type instanceof ConstructedType constructedType &&
                constructedType.constructor() == LIST_CONSTRUCTOR
        ) {
            return Optional.of(constructedType.args().get(0));
        } else {
            return Optional.empty();
        }
    }

    public static StructuredType map(Type keyType, Type valueType) {
        return construct(MAP_CONSTRUCTOR, List.of(keyType, valueType));
    }

    public static StructuredType member(Type receiverType, Type memberType) {
        return construct(MEMBER_CONSTRUCTOR, List.of(receiverType, memberType));
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

    public static boolean isMetaType(Type type) {
        return type instanceof TypeLevelValueType typeLevelValueType &&
            typeLevelValueType.value() instanceof Type;
    }

    public static TypeLevelValueType typeLevelValueType(TypeLevelValue value) {
        return new TypeLevelValueType(value);
    }

    public static TypeLevelValueType typeConstructorType(TypeConstructor typeConstructor) {
        return new TypeLevelValueType(typeConstructor);
    }

    public static EnumType enumType(NamespaceId namespaceId, String name, List<String> members) {
        return new EnumType(namespaceId, name, members);
    }

    public static FunctionType functionType(List<Type> positionalParams, Type returnType) {
        return new FunctionType(new ParamTypes(positionalParams, List.of()), returnType);
    }

    public static InterfaceType interfaceType(NamespaceId namespaceId, String name) {
        return new InterfaceType(namespaceId, name, false);
    }

    public static InterfaceType unsealedInterfaceType(NamespaceId namespaceId, String name) {
        return new InterfaceType(namespaceId, name, false);
    }

    public static ConstructorType constructorType(
        List<TypeParameter> typeLevelParams,
        List<Type> positionalParams,
        StructuredType returnType
    ) {
        return new ConstructorType(
            returnType.namespaceId(),
            Optional.of(typeLevelParams),
            new ParamTypes(positionalParams, List.of()),
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
            returnType.namespaceId(),
            Optional.of(typeLevelParams),
            new ParamTypes(positionalParams, List.of()),
            returnType,
            visibility
        );
    }

    public static ConstructorType constructorType(
        List<Type> positionalParams,
        StructuredType returnType
    ) {
        return new ConstructorType(
            returnType.namespaceId(),
            Optional.empty(),
            new ParamTypes(positionalParams, List.of()),
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
            returnType.namespaceId(),
            Optional.empty(),
            new ParamTypes(positionalParams, List.of()),
            returnType,
            visibility
        );
    }

    public static MethodType methodType(NamespaceId namespaceId, List<TypeParameter> typeLevelParams, List<Type> positionalParams, Type returnType) {
        return methodType(
            namespaceId,
            typeLevelParams,
            positionalParams,
            List.of(),
            returnType
        );
    }

    public static MethodType methodType(
        NamespaceId namespaceId,
        List<TypeParameter> typeLevelParams,
        List<Type> positionalParams,
        List<NamedParamType> namedParams,
        Type returnType
    ) {
        return new MethodType(
            namespaceId,
            Optional.of(typeLevelParams),
            new ParamTypes(positionalParams, namedParams),
            returnType,
            Visibility.PUBLIC
        );
    }

    public static MethodType methodType(StructuredType type, List<TypeParameter> typeLevelParams, List<Type> positionalParams, Type returnType) {
        return methodType(type.namespaceId(), typeLevelParams, positionalParams, returnType);
    }

    public static MethodType methodType(TypeConstructor typeConstructor, List<TypeParameter> typeLevelParams, List<Type> positionalParams, Type returnType) {
        return methodType(typeConstructor.genericType(), typeLevelParams, positionalParams, returnType);
    }

    public static MethodType methodType(NamespaceId namespaceId, List<Type> positionalParams, Type returnType) {
        return new MethodType(
            namespaceId,
            Optional.empty(),
            new ParamTypes(positionalParams, List.of()),
            returnType,
            Visibility.PUBLIC
        );
    }

    public static MethodType methodType(StructuredType type, List<Type> positionalParams, Type returnType) {
        return methodType(type.namespaceId(), positionalParams, returnType);
    }

    public static MethodType methodType(TypeConstructor typeConstructor, List<Type> positionalParams, Type returnType) {
        return methodType(typeConstructor.genericType(), positionalParams, returnType);
    }

    public static NamedParamType namedParam(String name, Type type) {
        return new NamedParamType(name, type);
    }

    public static StaticFunctionType staticFunctionType(
        NamespaceId namespaceId,
        String functionName,
        List<TypeParameter> typeLevelParams,
        ParamTypes paramTypes,
        Type returnType
    ) {
        return new StaticFunctionType(
            namespaceId,
            functionName,
            Optional.of(typeLevelParams),
            paramTypes,
            returnType,
            Visibility.PUBLIC
        );
    }

    public static StaticFunctionType staticFunctionType(
        NamespaceId namespaceId,
        String functionName,
        List<Type> positionalParams,
        Type returnType
    ) {
        return staticFunctionType(
            namespaceId,
            functionName,
            positionalParams,
            returnType,
            Visibility.PUBLIC
        );
    }

    public static StaticFunctionType staticFunctionType(
        NamespaceId namespaceId,
        String functionName,
        List<Type> positionalParams,
        List<NamedParamType> namedParams,
        Type returnType
    ) {
        return staticFunctionType(
            namespaceId,
            functionName,
            positionalParams,
            namedParams,
            returnType,
            Visibility.PUBLIC
        );
    }

    public static StaticFunctionType staticFunctionType(
        NamespaceId namespaceId,
        String functionName,
        List<Type> positionalParams,
        Type returnType,
        Visibility visibility
    ) {
        return new StaticFunctionType(
            namespaceId,
            functionName,
            Optional.empty(),
            new ParamTypes(positionalParams, List.of()),
            returnType,
            visibility
        );
    }

    public static StaticFunctionType staticFunctionType(
        NamespaceId namespaceId,
        String functionName,
        List<Type> positionalParams,
        List<NamedParamType> namedParams,
        Type returnType,
        Visibility visibility
    ) {
        return new StaticFunctionType(
            namespaceId,
            functionName,
            Optional.empty(),
            new ParamTypes(positionalParams, namedParams),
            returnType,
            visibility
        );
    }

    public static RecordType recordType(NamespaceId namespaceId, String name) {
        return new RecordType(namespaceId, name);
    }

    public static InterfaceType sealedInterfaceType(NamespaceId namespaceId, String name) {
        return new InterfaceType(namespaceId, name, true);
    }

    public static boolean isSealedInterfaceType(Type type) {
        return type instanceof InterfaceType interfaceType && interfaceType.isSealed();
    }

    public static StructuredType construct(TypeConstructor constructor, List<? extends Type> args) {
        // TODO: check args
        return new ConstructedType(constructor, args);
    }

    public static Type commonSupertype(Type left, Type right) {
        return TypeUnifier.commonSupertype(left, right);
    }

    public static Type commonSupertype(List<Type> types, Type defaultType) {
        return types.stream()
            .reduce((x, y) -> commonSupertype(x, y))
            .orElse(defaultType);
    }

    public static Type commonSubtype(Type left, Type right) {
        return TypeUnifier.commonSubtype(left, right);
    }
}
