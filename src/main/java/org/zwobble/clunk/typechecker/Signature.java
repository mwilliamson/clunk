package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;

sealed interface Signature {
    List<Type> positionalParams();
    Type returnType();
}

record SignatureConstructorRecord(List<Type> positionalParams, RecordType type) implements Signature {
    @Override
    public Type returnType() {
        return type;
    }
}

record SignatureConstructorStringBuilder() implements Signature {
    @Override
    public List<Type> positionalParams() {
        return List.of();
    }

    @Override
    public Type returnType() {
        return Types.STRING_BUILDER;
    }
}

record SignatureMethod(FunctionType type) implements Signature {
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
    public List<Type> positionalParams() {
        return type.positionalParams();
    }

    @Override
    public Type returnType() {
        return type.returnType();
    }
}

