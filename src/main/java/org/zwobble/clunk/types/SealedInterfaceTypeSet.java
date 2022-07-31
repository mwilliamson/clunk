package org.zwobble.clunk.types;

public class SealedInterfaceTypeSet implements TypeSet {
    public static final SealedInterfaceTypeSet INSTANCE = new SealedInterfaceTypeSet();

    private SealedInterfaceTypeSet() {

    }

    @Override
    public String describe() {
        return "sealed interface";
    }
}
