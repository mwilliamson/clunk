package org.zwobble.clunk.types;

public record NamedParamType(String name, Type type) {
    public NamedParamType replace(TypeMap typeMap) {
        return new NamedParamType(name, type.replace(typeMap));
    }
}
