package org.zwobble.clunk.types;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public record ConstructedType(TypeConstructor constructor, List<Type> args) implements StructuredType {
    @Override
    public String describe() {
        var argsString = args.stream()
            .map(arg -> arg.describe())
            .collect(Collectors.joining(", "));

        return constructor.describe() + "[" + argsString + "]";
    }

    @Override
    public StructuredType replace(TypeMap typeMap) {
        return new ConstructedType(
            constructor,
            args.stream()
                .map(arg -> arg.replace(typeMap))
                .toList()
        );
    }

    @Override
    public NamespaceName namespaceName() {
        return constructor.genericType().namespaceName();
    }

    @Override
    public String name() {
        return constructor.genericType().name();
    }

    public TypeMap typeMap() {
        var params = constructor().params();
        var args = args();

        var typeMap = new HashMap<TypeParameter, Type>();
        for (var i = 0; i < params.size(); i++) {
            var param = params.get(i);
            var arg = args.get(i);
            typeMap.put(param, arg);
        }

        return new TypeMap(typeMap);
    }
}
