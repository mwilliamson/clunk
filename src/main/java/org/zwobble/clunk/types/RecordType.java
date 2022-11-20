package org.zwobble.clunk.types;

public record RecordType(NamespaceName namespaceName, String name, Visibility constructorVisibility) implements StructuredType {
    @Override
    public String describe() {
        return namespaceName + "." + name;
    }

    @Override
    public StructuredType replace(TypeMap typeMap) {
        return this;
    }

    public boolean isPrivate() {
        return constructorVisibility == Visibility.PRIVATE;
    }
}
