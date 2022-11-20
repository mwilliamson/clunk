package org.zwobble.clunk.types;

import java.util.List;
import java.util.stream.Collectors;

public record ConstructedType(TypeConstructor constructor, List<? extends Type> args) implements StructuredType {
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
        return TypeMap.from(constructor().params(), args());
    }
}
