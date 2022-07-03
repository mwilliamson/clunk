package org.zwobble.clunk.types;

public record ListType(Type elementType) implements Type {
    @Override
    public String describe() {
        return "List[" + elementType.describe() + "]";
    }
}
