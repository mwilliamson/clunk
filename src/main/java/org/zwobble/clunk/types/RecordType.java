package org.zwobble.clunk.types;

public record RecordType(NamespaceName namespaceName, String name) implements Type {
    @Override
    public String describe() {
        return namespaceName + "." + name;
    }
}
