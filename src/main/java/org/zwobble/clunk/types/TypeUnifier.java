package org.zwobble.clunk.types;

public class TypeUnifier {
    private TypeUnifier() {
    }

    public static Type unify(Type left, Type right) {
        if (left.equals(right)) {
            return left;
        } else {
            return Types.OBJECT;
        }
    }
}
