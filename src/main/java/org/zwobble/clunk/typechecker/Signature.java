package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Optional;

sealed interface Signature permits SignatureNonGeneric, SignatureGeneric {
}

sealed interface SignatureNonGeneric extends Signature {
    List<Type> positionalParams();
    Type returnType();
}

sealed interface SignatureGeneric extends Signature {
    List<TypeParameter> typeParams();
    SignatureNonGeneric typeArgs(List<Type> typeArgs);
}

record SignatureConstructorRecord(List<Type> positionalParams, RecordType type) implements SignatureNonGeneric {
    @Override
    public Type returnType() {
        return type;
    }
}

record SignatureConstructorStringBuilder() implements SignatureNonGeneric {
    @Override
    public List<Type> positionalParams() {
        return List.of();
    }

    @Override
    public Type returnType() {
        return Types.STRING_BUILDER;
    }
}

record SignatureGenericMethod(MethodType type) implements SignatureGeneric {
    @Override
    public List<TypeParameter> typeParams() {
        return type.typeLevelParams().get();
    }

    @Override
    public SignatureNonGeneric typeArgs(List<Type> typeArgs) {
        // TODO: check args? In practice, this has already been done, but
        // there's no guarantee we won't accidentally call this in other cases.
        var typeMap = TypeMap.from(type.typeLevelParams().orElseThrow(), typeArgs);

        return new SignatureNonGenericMethod(new MethodType(
            Optional.empty(),
            type.positionalParams().stream()
                .map(param -> param.replace(typeMap))
                .toList(),
            type.returnType().replace(typeMap)
        ));
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

