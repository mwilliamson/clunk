package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;

sealed interface Signature permits SignatureNonGeneric, SignatureGeneric {
}

sealed interface SignatureNonGeneric extends Signature {
    List<Type> positionalParams();
    Type returnType();
}

sealed interface SignatureGeneric extends Signature {
    List<TypeParameter> typeParams();
    Type typeArgs(List<Type> typeArgs);
}

// TODO: merge into method/static function signatures?
record SignatureConstructorRecord(MethodType constructorType) implements SignatureNonGeneric {
    @Override
    public List<Type> positionalParams() {
        return constructorType.positionalParams();
    }

    @Override
    public Type returnType() {
        return constructorType.returnType();
    }
}

record SignatureGenericMethod(MethodType type) implements SignatureGeneric {
    @Override
    public List<TypeParameter> typeParams() {
        return type.typeLevelParams().get();
    }

    @Override
    public Type typeArgs(List<Type> typeArgs) {
        // TODO: check args? In practice, this has already been done, but
        // there's no guarantee we won't accidentally call this in other cases.
        var typeMap = TypeMap.from(type.typeLevelParams().orElseThrow(), typeArgs);

        return type.withoutTypeParams().replace(typeMap);
    }
}

record SignatureNonGenericMethod(MethodType type) implements SignatureNonGeneric {
    @Override
    public List<Type> positionalParams() {
        return type.positionalParams();
    }

    @Override
    public Type returnType() {
        return type.returnType();
    }
}

record SignatureStaticFunction(StaticFunctionType type) implements SignatureNonGeneric {
    @Override
    public List<Type> positionalParams() {
        return type.positionalParams();
    }

    @Override
    public Type returnType() {
        return type.returnType();
    }
}

