package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;

sealed interface Signature permits SignatureNonGeneric, SignatureGeneric {
    CallableType type();

    default int positionalParamCount() {
        return type().positionalParams().size();
    }

    default Type positionalParam(int index) {
        return type().positionalParams().get(index);
    }

    default List<Type> positionalParams() {
        return type().params().positional();
    }

    default List<NamedParamType> namedParams() {
        return type().params().named();
    }

    default Type returnType() {
        return type().returnType();
    }
}

record SignatureGeneric(CallableType type) implements Signature {
    public List<TypeParam> typeParams() {
        return type.typeLevelParams().orElseThrow();
    }

    public SignatureNonGeneric typeArgs(List<? extends Type> typeArgs) {
        // TODO: check args? In practice, this has already been done, but
        // there's no guarantee we won't accidentally call this in other cases.
        var typeMap = TypeMap.from(type.typeLevelParams().orElseThrow(), typeArgs);

        return new SignatureNonGeneric((CallableType) type.withoutTypeParams().replace(typeMap));
    }
}

record SignatureNonGeneric(CallableType type) implements Signature {
}
