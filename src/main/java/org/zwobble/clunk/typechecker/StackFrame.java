package org.zwobble.clunk.typechecker;

import org.pcollections.PMap;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.P;

import java.util.Map;
import java.util.Optional;

public record StackFrame(
    Optional<NamespaceId> namespaceId,
    Optional<Type> returnType,
    PMap<String, Variable> environment
) {
    public static StackFrame body(Map<String, Variable> environment) {
        return new StackFrame(
            Optional.empty(),
            Optional.empty(),
            P.copyOf(environment)
        );
    }

    public static StackFrame builtins(Map<String, Variable> environment) {
        return new StackFrame(
            Optional.empty(),
            Optional.empty(),
            P.copyOf(environment)
        );
    }

    public static StackFrame function(Type returnType) {
        return new StackFrame(
            Optional.empty(),
            Optional.of(returnType),
            P.map()
        );
    }

    public static StackFrame namespace(NamespaceId namespaceId) {
        return namespace(namespaceId, Map.of());
    }

    public static StackFrame namespace(NamespaceId namespaceId, Map<String, Variable> environment) {
        return new StackFrame(
            Optional.of(namespaceId),
            Optional.empty(),
            P.copyOf(environment)
        );
    }

    public static StackFrame test() {
        return new StackFrame(
            Optional.empty(),
            Optional.empty(),
            P.copyOf(Map.of())
        );
    }

    public static StackFrame testSuite() {
        return new StackFrame(
            Optional.empty(),
            Optional.empty(),
            P.map()
        );
    }

    public StackFrame addVariable(String name, Variable variable, Source source) {
        if (this.environment.containsKey(name)) {
            throw new VariableAlreadyDefinedError(name, source);
        }

        return new StackFrame(namespaceId, returnType, environment.plus(name, variable));
    }

    public StackFrame updateVariable(String name, Variable variable, Source source) {
        return new StackFrame(namespaceId, returnType, environment.plus(name, variable));
    }
}
