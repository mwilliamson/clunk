package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;

public sealed interface CallableType extends Type permits ConstructorType, MethodType, StaticFunctionType {
    NamespaceId namespaceId();
    Optional<List<TypeParameter>> typeLevelParams();
    ParamTypes params();
    Type returnType();
    Visibility visibility();

    CallableType withoutTypeParams();

    default List<Type> positionalParams() {
        return params().positional();
    }
}
