package org.zwobble.clunk.types;

public record InterfaceType(NamespaceName namespaceName, String name) implements StructuredType {
    @Override
    public String describe() {
        return namespaceName + "." + name;
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
