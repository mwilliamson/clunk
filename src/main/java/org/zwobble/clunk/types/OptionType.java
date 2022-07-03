package org.zwobble.clunk.types;

public record OptionType(Type elementType) implements Type {
    @Override
    public String describe() {
        return "Option[" + elementType.describe() + "]";
    }
}
