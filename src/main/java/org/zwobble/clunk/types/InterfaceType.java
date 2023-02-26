package org.zwobble.clunk.types;

public record InterfaceType(
    NamespaceId namespaceId,
    String name,
    boolean isSealed
) implements StructuredType {
    @Override
    public String describe() {
        return namespaceId + "." + name;
    }

    @Override
    public StructuredType replace(TypeMap typeMap) {
        return this;
    }
}
