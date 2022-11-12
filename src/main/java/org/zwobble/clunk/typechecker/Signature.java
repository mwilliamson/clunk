package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Optional;

sealed interface Signature {
    Optional<List<TypeParameter>> typeParams();
    List<Type> positionalParams();
    Type returnType();

    Signature typeArgs(List<Type> typeArgs);
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

    @Override
    public Signature typeArgs(List<Type> typeArgs) {
        throw new UnsupportedOperationException();
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

    @Override
    public Signature typeArgs(List<Type> typeArgs) {
        throw new UnsupportedOperationException();
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

    @Override
    public Signature typeArgs(List<Type> typeArgs) {
        // TODO: check args? In practice, this has already been done, but
        // there's no guarantee we won't accidentally call this in other cases.
        var typeMap = TypeMap.from(type.typeLevelParams().orElseThrow(), typeArgs);

        return new SignatureMethod(new MethodType(
            Optional.empty(),
            type.positionalParams().stream()
                .map(param -> param.replace(typeMap))
                .toList(),
            type.returnType().replace(typeMap)
        ));
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

    @Override
    public Signature typeArgs(List<Type> typeArgs) {
        throw new UnsupportedOperationException();
    }
}

