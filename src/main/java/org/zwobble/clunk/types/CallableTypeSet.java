package org.zwobble.clunk.types;

public class CallableTypeSet implements TypeSet {
    public static final CallableTypeSet INSTANCE = new CallableTypeSet();

    private CallableTypeSet() {
    }

    @Override
    public String describe() {
        return "callable";
    }
}
