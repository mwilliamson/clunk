package org.zwobble.clunk.types;

import java.util.List;
import java.util.stream.Collectors;

public record ConstructedType(TypeConstructor constructor, List<Type> args) implements Type {
    @Override
    public String describe() {
        var argsString = args.stream()
            .map(arg -> arg.describe())
            .collect(Collectors.joining(", "));

        return constructor.describe() + "[" + argsString + "]";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        // TODO: implement properly
        return this;
    }
}
