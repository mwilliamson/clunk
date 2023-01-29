package org.zwobble.clunk.types;

public record InterfaceType(
    NamespaceName namespaceName,
    String name,
    boolean isSealed
) implements StructuredType {
    @Override
    public String describe() {
        return namespaceName + "." + name;
    }

    @Override
    public StructuredType replace(TypeMap typeMap) {
        return this;
    }
}
