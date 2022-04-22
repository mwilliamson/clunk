package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record TypeCheckerContext(Optional<Type> returnType, Map<String, Type> environment) {
    public static TypeCheckerContext stub() {
        return new TypeCheckerContext(Optional.empty(), Map.of());
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

    public Type resolveType(String value) {
        return switch (value) {
            case "Bool" -> BoolType.INSTANCE;
            case "Int" -> IntType.INSTANCE;
            case "String" -> StringType.INSTANCE;
            default -> throw new RuntimeException("TODO");
        };
    }

    public Type typeOf(String name) {
        return environment.get(name);
    }
}
