package org.zwobble.clunk.types;

public record RecordType(NamespaceId namespaceId, String name) implements StructuredType {
    @Override
    public String describe() {
        return namespaceId + "." + name;
    }

    @Override
    public StructuredType replace(TypeMap typeMap) {
        return this;
    }
}
