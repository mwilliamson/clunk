package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;

public sealed interface CallableType extends Type permits ConstructorType, MethodType, StaticFunctionType {
    NamespaceName namespaceName();
    Optional<List<TypeParameter>> typeLevelParams();
    List<Type> positionalParams();
    Type returnType();
    Visibility visibility();

    CallableType withoutTypeParams();
}
