package org.zwobble.clunk.types;

public class TypeUnifier {
    private TypeUnifier() {
    }

    public static Type unify(Type left, Type right) {
        if (left.equals(right)) {
            return left;
        } else if (left.equals(Types.NOTHING)) {
            return right;
        } else if (right.equals(Types.NOTHING)) {
            return left;
        } else {
            return Types.OBJECT;
        }
    }
}
