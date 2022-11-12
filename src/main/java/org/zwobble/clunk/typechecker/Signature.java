package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Optional;

sealed interface Signature {
    Optional<List<TypeParameter>> typeParams();
    List<Type> positionalParams();
    Type returnType();
}

record SignatureConstructorRecord(List<Type> positionalParams, RecordType type) implements Signature {
    @Override
    public Optional<List<TypeParameter>> typeParams() {
        return Optional.empty();
    }

    @Override
    public Type returnType() {
        return type;
    }
}

record SignatureConstructorStringBuilder() implements Signature {
    @Override
    public Optional<List<TypeParameter>> typeParams() {
        return Optional.empty();
    }

    @Override
    public List<Type> positionalParams() {
        return List.of();
    }

    @Override
    public Type returnType() {
        return Types.STRING_BUILDER;
    }
}

record SignatureMethod(MethodType type) implements Signature {
    @Override
    public Optional<List<TypeParameter>> typeParams() {
        return type.typeLevelParams();
    }

    @Override
    public List<Type> positionalParams() {
        return type.positionalParams();
    }

    @Override
    public Type returnType() {
        return type.returnType();
    }
}

record SignatureStaticFunction(StaticFunctionType type) implements Signature {
    @Override
    public Optional<List<TypeParameter>> typeParams() {
        return Optional.empty();
    }

    @Override
    public List<Type> positionalParams() {
        return type.positionalParams();
    }

    @Override
    public Type returnType() {
        return type.returnType();
    }
}

