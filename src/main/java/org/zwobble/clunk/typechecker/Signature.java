package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Optional;

sealed interface Signature permits SignatureNonGeneric, SignatureGeneric {
}

record SignatureGeneric(CallableType type) implements Signature {
    public List<TypeParameter> typeParams() {
        return type.typeLevelParams().orElseThrow();
    }

    public Type typeArgs(List<Type> typeArgs) {
        // TODO: check args? In practice, this has already been done, but
        // there's no guarantee we won't accidentally call this in other cases.
        var typeMap = TypeMap.from(type.typeLevelParams().orElseThrow(), typeArgs);

        return type.withoutTypeParams().replace(typeMap);
    }
}

record SignatureNonGeneric(CallableType type) implements Signature {
    public List<Type> positionalParams() {
        return type.params().positional();
    }

    public Optional<NamedParamType> namedParam(String name) {
        return type.params().named()
            .stream()
            .filter(param -> param.name().equals(name))
            .findFirst();
    }

    public Type returnType() {
        return type.returnType();
    }
}
