package org.zwobble.clunk.types;

public class TypeUnifier {
    private TypeUnifier() {
    }

    public static Type commonSubtype(Type left, Type right) {
        if (left.equals(right)) {
            return left;
        } else if (left.equals(Types.OBJECT)) {
            return right;
        } else if (right.equals(Types.OBJECT)) {
            return left;
        } else {
            return Types.NOTHING;
        }
    }

    public static Type commonSupertype(Type left, Type right) {
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
