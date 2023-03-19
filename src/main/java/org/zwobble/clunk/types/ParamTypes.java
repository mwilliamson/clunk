package org.zwobble.clunk.types;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ParamTypes(
    List<Type> positional,
    List<NamedParamType> named
) {
    public static ParamTypes empty() {
        return new ParamTypes(List.of(), List.of());
    }

    public static ParamTypes of(List<Type> positional) {
        return new ParamTypes(positional, List.of());
    }

    public String describe() {
        return Stream.concat(
            positional.stream()
                .map(param -> param.describe()),
            named.stream()
                .map(param -> "." + param.name() + ": " + param.type().describe())
        ).collect(Collectors.joining(", "));
    }

    public ParamTypes replace(TypeMap typeMap) {
        return new ParamTypes(
            positional.stream()
                .map(param -> param.replace(typeMap))
                .toList(),
            named.stream()
                .map(param -> param.replace(typeMap))
                .toList()
        );
    }
}
