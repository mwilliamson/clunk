package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record StackFrame(
    Optional<NamespaceName> namespaceName,
    Optional<Type> returnType,
    Map<String, Type> environment
) {
    public static StackFrame builtins(Map<String, Type> environment) {
        return new StackFrame(
            Optional.empty(),
            Optional.empty(),
            environment
        );
    }

    public static StackFrame function(Type returnType) {
        return new StackFrame(
            Optional.empty(),
            Optional.of(returnType),
            Map.of()
        );
    }

    public static StackFrame namespace(NamespaceName namespaceName) {
        return namespace(namespaceName, Map.of());
    }

    public static StackFrame namespace(NamespaceName namespaceName, Map<String, Type> environment) {
        return new StackFrame(
            Optional.of(namespaceName),
            Optional.empty(),
            environment
        );
    }

    public static StackFrame test() {
        return new StackFrame(
            Optional.empty(),
            Optional.empty(),
            Map.of()
        );
    }

    public StackFrame updateType(String name, Type type) {
        var environment = new HashMap<>(this.environment);
        environment.put(name, type);
        return new StackFrame(namespaceName, returnType, environment);
    }
}
