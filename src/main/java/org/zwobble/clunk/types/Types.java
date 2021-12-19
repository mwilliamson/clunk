package org.zwobble.clunk.types;

public class Types {
    private Types() {

    }

    public static boolean isSubType(Type subType, Type superType) {
        return subType.equals(superType);
    }
}
