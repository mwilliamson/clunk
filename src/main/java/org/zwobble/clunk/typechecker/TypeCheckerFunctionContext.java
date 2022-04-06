package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record TypeCheckerFunctionContext(Optional<Type> returnType, Map<String, Type> environment) {
    public static TypeCheckerFunctionContext stub() {
        return enterFunction(BoolType.INSTANCE);
    }

    public static TypeCheckerFunctionContext enterFunction(Type returnType) {
        return new TypeCheckerFunctionContext(Optional.of(returnType), Map.of());
    }

    public static TypeCheckerFunctionContext enterTest() {
        return new TypeCheckerFunctionContext(Optional.empty(), Map.of());
    }

    public TypeCheckerFunctionContext updateType(String name, Type type) {
        var environment = new HashMap<>(this.environment);
        environment.put(name, type);
        return new TypeCheckerFunctionContext(returnType, environment);
    }

    public Type typeOf(String name) {
        return environment.get(name);
    }
}
