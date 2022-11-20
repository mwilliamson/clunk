package org.zwobble.clunk.types;

public record RecordType(NamespaceName namespaceName, String name) implements StructuredType {
    @Override
    public String describe() {
        return namespaceName + "." + name;
    }

    @Override
    public StructuredType replace(TypeMap typeMap) {
        return this;
    }
}
