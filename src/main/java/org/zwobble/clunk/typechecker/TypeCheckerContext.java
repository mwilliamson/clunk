package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.MetaType;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record TypeCheckerContext(
    Optional<Type> returnType,
    Map<String, Type> environment,
    Map<NamespaceName, NamespaceType> namespaceTypes
) {
    public static final TypeCheckerContext EMPTY = new TypeCheckerContext(Optional.empty(), Map.of(), Map.of());

    public static TypeCheckerContext stub() {
        return new TypeCheckerContext(Optional.empty(), Builtins.ENVIRONMENT, Map.of());
    }

    public TypeCheckerContext enterFunction(Type returnType) {
        return enter(Optional.of(returnType));
    }

    public TypeCheckerContext enterTest() {
        return enter(Optional.empty());
    }

    private TypeCheckerContext enter(Optional<Type> returnType) {
        return new TypeCheckerContext(returnType, environment, namespaceTypes);
    }

    public TypeCheckerContext updateNamespaceType(NamespaceType namespaceType) {
        var namespaceTypes = new HashMap<>(this.namespaceTypes);
        namespaceTypes.put(namespaceType.name(), namespaceType);
        return new TypeCheckerContext(returnType, environment, namespaceTypes);
    }

    public Optional<NamespaceType> typeOfNamespace(NamespaceName name) {
        return Optional.ofNullable(namespaceTypes.get(name));
    }

    public TypeCheckerContext withEnvironment(Map<String, Type> environment) {
        return new TypeCheckerContext(returnType, environment, namespaceTypes);
    }

    public TypeCheckerContext updateType(String name, Type type) {
        var environment = new HashMap<>(this.environment);
        environment.put(name, type);
        return new TypeCheckerContext(returnType, environment, namespaceTypes);
    }

    public Type resolveType(String name, Source source) {
        var type = typeOf(name, source);
        if (type instanceof MetaType) {
            return ((MetaType) type).type();
        } else {
            throw new RuntimeException("TODO");
        }
    }

    public Type typeOf(String name, Source source) {
        var type = environment.get(name);
        if (type == null) {
            throw new SourceError("unknown variable: " + name, source);
        }
        return type;
    }
}
