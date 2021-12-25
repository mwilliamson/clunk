package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.Type;

import java.util.HashMap;
import java.util.Map;

public record TypeCheckerFunctionContext(Type returnType, Map<String, Type> environment) {
    public static TypeCheckerFunctionContext stub() {
        return enter(BoolType.INSTANCE);
    }

    public static TypeCheckerFunctionContext enter(Type returnType) {
        return new TypeCheckerFunctionContext(returnType, Map.of());
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
