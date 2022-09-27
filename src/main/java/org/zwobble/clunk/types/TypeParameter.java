package org.zwobble.clunk.types;

public record TypeParameter(String name, Variance variance) {
    public static TypeParameter covariant(String name) {
        return new TypeParameter(name, Variance.COVARIANT);
    }

    public static TypeParameter invariant(String name) {
        return new TypeParameter(name, Variance.INVARIANT);
    }
}
