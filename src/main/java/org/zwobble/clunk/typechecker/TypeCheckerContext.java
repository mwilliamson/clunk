package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.types.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record TypeCheckerContext(Optional<Type> returnType, Map<String, Type> environment) {
    public static TypeCheckerContext stub() {
        return new TypeCheckerContext(Optional.empty(), Builtins.ENVIRONMENT);
    }

    public TypeCheckerContext enterFunction(Type returnType) {
        return enter(Optional.of(returnType));
    }

    public TypeCheckerContext enterTest() {
        return enter(Optional.empty());
    }

    private TypeCheckerContext enter(Optional<Type> returnType) {
        return new TypeCheckerContext(returnType, Map.of());
    }

    public TypeCheckerContext updateType(String name, Type type) {
        var environment = new HashMap<>(this.environment);
        environment.put(name, type);
        return new TypeCheckerContext(returnType, environment);
    }

    public Type resolveType(String name) {
        var type = typeOf(name);
        if (type instanceof MetaType) {
            return ((MetaType) type).type();
        } else {
            throw new RuntimeException("TODO");
        }
    }

    public Type typeOf(String name) {
        return environment.get(name);
    }
}
