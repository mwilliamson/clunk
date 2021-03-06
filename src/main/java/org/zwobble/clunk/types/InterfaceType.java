package org.zwobble.clunk.types;

public record InterfaceType(NamespaceName namespaceName, String name) implements Type {
    @Override
    public String describe() {
        return namespaceName + "." + name;
    }
}
